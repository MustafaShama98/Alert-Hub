package org.example.securityservice.service;

import lombok.RequiredArgsConstructor;
import org.example.securityservice.dto.AuthRequest;
import org.example.securityservice.dto.AuthResponse;
import org.example.securityservice.dto.RegisterRequest;
import org.example.securityservice.config.JWTService;
import org.example.securityservice.dto.UserRequest;
import org.example.securityservice.feign.UsersFeignClient;
import org.example.securityservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.securityservice.model.User;
import org.example.securityservice.model.Permission;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final MyUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    private UsersFeignClient usersFeignClient;

    // This class will handle the authentication logic
    // It will interact with the UserRepo to fetch user details
    // and validate credentials.

    public AuthResponse authenticate(AuthRequest request) {
        // First authenticate using email and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Load user details using email
        var user = (User) userDetailsService.loadUserByEmail(request.getEmail());
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
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

        // Save the user first
        user = userRepository.save(user);

        // Assign default permissions
        Permission[] defaultPermissions = {
            Permission.READ
        };

        // Add all default permissions at once
        permissionService.setUserPermissions(user.getId(), defaultPermissions);
        
        return generateAuthResponse(user);
    }

    // Helper method to generate auth response
    public AuthResponse generateAuthResponse(User user) {
        // Set permissions in user object for authorities
        user.setPermissions(permissionService.getUserPermissions(user.getId()));
        
        // Create claims with user information and permissions
        Map<String, Object> extraClaims = new HashMap<>();
        String permissions = permissionService.getUserPermissionsAsString(user.getId());
        
        extraClaims.put("userId", user.getId().toString());
        extraClaims.put("email", user.getEmail());
        extraClaims.put("permissions", permissions);
        
        // Generate token using the user details and claims
        var token = jwtService.generateToken(extraClaims, user);
        
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
