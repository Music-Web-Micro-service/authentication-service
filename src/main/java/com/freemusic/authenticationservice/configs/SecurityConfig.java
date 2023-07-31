package com.freemusic.authenticationservice.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
//@EnableGlobalAuthentication
public class SecurityConfig {

    @Value("${classpath:jwtPublic.rsa.pem}")
    private RSAPublicKey key;

    /**
     *
     * decoder
     * */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(this.key).build();
        //NimbusJwtDecoder.withPublicKey(this.key).build();
        return jwtDecoder;
    }


    /**
     *
     * tell spring security we dont want default role
     * */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        final JwtAuthenticationConverter jac = new JwtAuthenticationConverter();
        jac.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jac;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        return httpSecurity.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/auth/**").permitAll()
                               // .requestMatchers()
                                .anyRequest().authenticated()
       //                         .antMatchers("/admin/**","/user/signup/**","/user/login/**").permitAll()
//                                .antMatchers("/**").hasRole("admin")
//                                .mvcMatchers("/findMusic/**").hasAnyRole("admin")
//                                .mvcMatchers("/music/findAllMusic/**").hasAnyRole("admin")
//                                .mvcMatchers("/music/delete/**").hasAnyRole("admin")
//                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .build();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // Here we set authentication for all endpoints
        return httpSecurity.authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()        )
                // Here we enable that we will accept JWTs
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .build();
    }
}
