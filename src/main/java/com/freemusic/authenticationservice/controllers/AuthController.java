package com.freemusic.authenticationservice.controllers;

import com.freemusic.authenticationservice.models.AuthUser;
import com.freemusic.authenticationservice.JwtTokenUtil;
import com.freemusic.authenticationservice.messages.LoginRequest;
import com.freemusic.authenticationservice.messages.RegistrationRequest;
import com.freemusic.authenticationservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest) throws Exception {
        if(registrationRequest.getEmail() == null && registrationRequest.getPhoneNumber() == null){
            return ResponseEntity.badRequest().body("Email and phone number cannot both be null.");
        }

        // Create a new user
        AuthUser newUser = new AuthUser();
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setPhoneNumber(registrationRequest.getPhoneNumber());

        newUser.setPassword(registrationRequest.getPassword());

        // After register, the newUser will contain the list of UserRole
        newUser = authService.register(newUser);

        // Create GrantedAuthorities list from user's roles
        List<GrantedAuthority> authorities = newUser.getUserRoleList().stream()
                .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName().toUpperCase()))
                .collect(Collectors.toList());

        // Generate token
        Map<String, Object> claims = new HashMap<>();
        String token = jwtTokenUtil.generateToken(newUser.getEmail(), claims, authorities);

        return ResponseEntity.ok(token);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
        // Validate user credentials
        UserDetails userDetails = authService.loadUserByUsername(loginRequest.getLogin());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            // Generate token
            Map<String, Object> claims = new HashMap<>();
            String token = jwtTokenUtil.generateToken(userDetails.getUsername(), claims, userDetails.getAuthorities());

            return ResponseEntity.ok(token);
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
