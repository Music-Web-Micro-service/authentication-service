package com.freemusic.authenticationservice.services;

import com.freemusic.authenticationservice.models.AuthUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthService {

    //public AuthToken login(String email, String password) throws UsernameNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    public AuthUser register(AuthUser newUser);
}
