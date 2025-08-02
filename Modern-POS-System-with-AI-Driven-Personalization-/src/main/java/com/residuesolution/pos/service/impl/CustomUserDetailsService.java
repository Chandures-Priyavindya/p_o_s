package com.residuesolution.pos.service.impl;

import com.residuesolution.pos.entity.UserEntity;
import com.residuesolution.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Attempting to authenticate user: " + email); // Add this line
        // Find user by email in the database
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        if (userEntity.isEmpty()) {
            System.out.println("User not found: " + email); // Add this line
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        System.out.println("User found: " + userEntity.get().getName()); // Add this line

        UserEntity user = userEntity.get();

        // Check if user is active
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + email);
        }

        // Create authority with ROLE_ prefix (Spring Security requirement)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        // Return Spring Security UserDetails object
        return User.builder()
                .username(user.getEmail()) // Using email as username
                .password(user.getPassword()) // This should be encoded password from database
                .authorities(Collections.singletonList(authority))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
}