package com.freemusic.authenticationservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtTokenUtil {

    @Value("${classpath:jwt.rsa.pem}")
    private RSAPrivateKey secretKey;

    private long expiration = 3600000;


    public String generateToken(String userId, Map<String, Object> claims, Collection<? extends GrantedAuthority> authorities) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        long now = System.currentTimeMillis();
        long exp = now + expiration;

        JwtBuilder jwtBuilder = Jwts.builder().setId(String.valueOf(userId)).
                setSubject(userId).setIssuedAt(new Date()).signWith(SignatureAlgorithm.RS256,secretKey);
        jwtBuilder.setExpiration(new Date(exp));

        // Iterate through the claims map and set each entry as a claim in the JWT
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            jwtBuilder.claim(entry.getKey(), entry.getValue());
        }

        // Add the authorities as a claim
        jwtBuilder.claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        String token = jwtBuilder.compact();
        return token;
    }


    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
}
