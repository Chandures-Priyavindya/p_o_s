package com.residuesolution.pos.controller;

import com.residuesolution.pos.dto.User;
import com.residuesolution.pos.service.UserService;
import com.residuesolution.pos.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
@RequiredArgsConstructor // ✅ ADD THIS - automatically creates constructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder; // ✅ ADD THIS LINE

    // ✅ NO NEED FOR MANUAL CONSTRUCTOR - Lombok creates it automatically

    // Create User (Admin Only)
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        boolean isAdded = userService.addUser(user);
        if (isAdded) {
            return ResponseEntity.ok("User added successfully");
        } else {
            throw new ResourceNotFoundException("User could not be added");
        }
    }

    // Get All Users (Admin and Manager)
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get Single User by ID (Admin and Manager)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }

    // Update User (Admin Only)
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable Integer id, @RequestBody User user) {
        boolean updated = userService.updateUser(id, user);
        if (updated) {
            return ResponseEntity.ok("User updated successfully");
        } else {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }

    // Delete User (Admin Only)
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }

    // Encode password endpoint (No authentication required for testing)
    @PostMapping("/encode-password")
    public ResponseEntity<String> encodePassword(@RequestBody Map<String, String> request) {
        String plainPassword = request.get("password");
        if (plainPassword == null || plainPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        String encodedPassword = passwordEncoder.encode(plainPassword);

        return ResponseEntity.ok("Encoded password: " + encodedPassword);
    }

    // Update user password (Admin Only)
    @PostMapping("/update-password/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateUserPassword(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> request) {

        String newPassword = request.get("password");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        // Update user password
        User user = new User();
        user.setPassword(newPassword);

        Boolean updated = userService.updateUser(userId, user);

        if (updated) {
            return ResponseEntity.ok("Password updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // Add this endpoint to check existing users (add after your existing methods)
    @GetMapping("/check-users")
    public ResponseEntity<?> checkExistingUsers() {
        try {
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "message", "No users found in database",
                        "count", 0,
                        "suggestion", "You need to add users to the database first"
                ));
            }

            // Return user info without passwords
            List<Map<String, ? extends Serializable>> userInfo = users.stream()
                    .map(user -> Map.of(
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "role", user.getRole(),
                            "isActive", user.isActive()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "message", "Found users in database",
                    "count", users.size(),
                    "users", userInfo
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "message", "Error checking users: " + e.getMessage(),
                    "suggestion", "Check database connection and user table"
            ));
        }
    }
}