package com.freemusic.authenticationservice.repositories;

import com.freemusic.authenticationservice.models.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserRepository extends JpaRepository<AuthUser,Integer> {
    AuthUser findByEmail(String email);
    AuthUser findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
