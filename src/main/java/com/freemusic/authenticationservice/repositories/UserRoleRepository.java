package com.freemusic.authenticationservice.repositories;

import com.freemusic.authenticationservice.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository  extends JpaRepository<UserRole,Integer> {
}
