package com.freemusic.authenticationservice.services.implement;

import com.freemusic.authenticationservice.models.AuthUser;
import com.freemusic.authenticationservice.models.Role;
import com.freemusic.authenticationservice.models.UserRole;
import com.freemusic.authenticationservice.JwtTokenUtil;
import com.freemusic.authenticationservice.repositories.AuthUserRepository;
import com.freemusic.authenticationservice.repositories.RoleRepository;
import com.freemusic.authenticationservice.repositories.UserRoleRepository;
import com.freemusic.authenticationservice.services.AuthService;
import com.freemusic.feignservice.clients.UserClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserClients userClients;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser user = authUserRepository.findByEmail(username);

        if (user == null) {
            user = authUserRepository.findByPhoneNumber(username);

            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        }

        // Load the user's roles
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (UserRole userRole : user.getUserRoleList()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName().toUpperCase()));
        }

        String AuthuserId = user.getAuthUserId()+"";
        // Convert AuthUser to a Spring Security User
        return new org.springframework.security.core.userdetails.User(AuthuserId, user.getPassword(), authorities);
    }

    @Transactional
    public AuthUser save(AuthUser user) {
        // Check if email or phone number is in use
        if (authUserRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        if (authUserRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is already in use");
        }

        String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        // Encode the password
        user.setPassword(encodedPassword);

        // Save the user
        user = authUserRepository.save(user);

        // Set the role to User
        Role userRole = roleRepository.findByName("User");
        if (userRole == null) {
            throw new IllegalArgumentException("User Role not found in the database");
        }

        UserRole newUserRole = new UserRole();
        newUserRole.setAuthUser(user);
        newUserRole.setRole(userRole);

        // Save the UserRole
        userRoleRepository.save(newUserRole);

        return user;
    }

    @Override
    public AuthUser register(AuthUser newUser) {
        return save(newUser);
    }
}

