package org.example.securityservice.service;

import lombok.RequiredArgsConstructor;
import org.example.securityservice.dto.AuthResponse;
import org.example.securityservice.dto.RegisterRequest;
import org.example.securityservice.dto.UserResponse;
import org.example.securityservice.model.Permission;
import org.example.securityservice.model.User;
import org.example.securityservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Transactional
    public AuthResponse createUser(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }

        // Create new user with email as the username
        var user = User.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Save the user
        user = userRepository.save(user);

        // By default, only give READ permission
        permissionService.setUserPermissions(user.getId(), Permission.READ);

        return authService.generateAuthResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        userRepository.delete(user);
    }

    @Transactional
    public void grantPermissions(Long userId, List<String> permissionStrings) {
        // Convert string permissions to Permission enum
        Permission[] permissions = permissionStrings.stream()
                .map(Permission::fromPermission)
                .toArray(Permission[]::new);

        permissionService.addUserPermissions(userId, permissions);
    }

    @Transactional
    public void revokePermissions(Long userId, List<String> permissionStrings) {
        // Get current permissions
        List<String> currentPermissions = permissionService.getUserPermissions(userId);
        
        // Remove the permissions to be revoked
        List<String> updatedPermissions = currentPermissions.stream()
                .filter(p -> !permissionStrings.contains(p))
                .collect(Collectors.toList());

        // Convert to Permission enum and update
        Permission[] permissions = updatedPermissions.stream()
                .map(Permission::fromPermission)
                .toArray(Permission[]::new);

        permissionService.setUserPermissions(userId, permissions);
    }

    public List<String> getUserPermissions(Long userId) {
        return permissionService.getUserPermissions(userId);
    }

    public List<String> getAllAvailablePermissions() {
        return Arrays.stream(Permission.values())
                .map(Permission::getPermission)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    List<String> userPermissions = permissionService.getUserPermissions(user.getId());
                    return UserResponse.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .username(user.getUsername())
                            .phone(user.getPhone())
                            .permissions(userPermissions)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        List<String> permissions = permissionService.getUserPermissions(userId);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .permissions(permissions)
                .build();
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        List<String> permissions = permissionService.getUserPermissions(user.getId());
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .permissions(permissions)
                .build();
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        List<String> permissions = permissionService.getUserPermissions(user.getId());
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .permissions(permissions)
                .build();
    }

    @Transactional
    public UserResponse updateUser(Long userId, RegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user = userRepository.save(user);
        List<String> permissions = permissionService.getUserPermissions(userId);
        
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .permissions(permissions)
                .build();
    }
} 