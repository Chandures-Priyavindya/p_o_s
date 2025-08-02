package com.residuesolution.pos.repository;

import com.residuesolution.pos.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    // Method to find user by email (used for authentication)
    Optional<UserEntity> findByEmail(String email);

    // Optional: Method to check if email exists (useful for registration)
    boolean existsByEmail(String email);
}