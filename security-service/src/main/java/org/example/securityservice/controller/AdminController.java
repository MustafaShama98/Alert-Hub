package org.example.securityservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.securityservice.dto.RegisterRequest;
import org.example.securityservice.dto.UserResponse;
import org.example.securityservice.model.Permission;
import org.example.securityservice.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/security/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('admin')")  // Requires admin permission for all endpoints
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
        log.info("📝 Creating new user: {}", request.getEmail());
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        log.info("🗑️ Deleting user with ID: {}", userId);
        adminService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/permissions")
    public ResponseEntity<?> grantPermissions(
            @PathVariable Long userId,
            @RequestBody List<String> permissions) {
        log.info("➕ Granting permissions {} to user {}", permissions, userId);
        adminService.grantPermissions(userId, permissions);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}/permissions")
    public ResponseEntity<?> revokePermissions(
            @PathVariable Long userId,
            @RequestBody List<String> permissions) {
        log.info("➖ Revoking permissions {} from user {}", permissions, userId);
        adminService.revokePermissions(userId, permissions);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<List<String>> getUserPermissions(@PathVariable Long userId) {
        log.info("🔍 Getting permissions for user {}", userId);
        return ResponseEntity.ok(adminService.getUserPermissions(userId));
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<String>> getAllAvailablePermissions() {
        log.info("📋 Getting all available permissions");
        return ResponseEntity.ok(adminService.getAllAvailablePermissions());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("📋 Getting all users");
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        log.info("🔍 Getting user by ID: {}", userId);
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("🔍 Getting user by email: {}", email);
        return ResponseEntity.ok(adminService.getUserByEmail(email));
    }

    @GetMapping("/users/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        log.info("🔍 Getting user by username: {}", username);
        return ResponseEntity.ok(adminService.getUserByUsername(username));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody RegisterRequest request) {
        log.info("✏️ Updating user with ID: {}", userId);
        return ResponseEntity.ok(adminService.updateUser(userId, request));
    }
} 