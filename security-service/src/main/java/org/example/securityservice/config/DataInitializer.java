package org.example.securityservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityservice.model.Permission;
import org.example.securityservice.model.User;
import org.example.securityservice.repository.UserRepository;
import org.example.securityservice.service.PermissionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    @PostConstruct
    @Transactional
    public void initializeData() {
        // Only initialize if no users exist
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping initialization");
            return;
        }

        log.info("Starting data initialization...");

        // Create admin user with all permissions
        User adminUser = createUser("admin@alerthub.com", "Admin User", "+1234567890");
        permissionService.addUserPermissions(adminUser.getId(), Permission.ADMIN);
        log.info("Created admin user: {}", adminUser.getEmail());

        // Create regular users with different permission combinations
        createRegularUser("john.dev@alerthub.com", "John Developer", "+1234567891", 
            Permission.CREATE_METRIC,
            Permission.READ);

        createRegularUser("sarah.qa@alerthub.com", "Sarah QA", "+1234567892",
            Permission.CREATE_METRIC,
            Permission.UPDATE_METRIC,
            Permission.READ);

        createRegularUser("mike.ops@alerthub.com", "Mike Operations", "+1234567893",
            Permission.CREATE_METRIC,
            Permission.UPDATE_METRIC,
            Permission.DELETE_METRIC,
            Permission.READ,
            Permission.TRIGGER_SCAN,
            Permission.TRIGGER_PROCESS);

        createRegularUser("lisa.dev@alerthub.com", "Lisa Developer", "+1234567894",
            Permission.CREATE_METRIC,
            Permission.READ,
            Permission.TRIGGER_EVALUATION);

        createRegularUser("tom.qa@alerthub.com", "Tom QA", "+1234567895",
            Permission.READ,
            Permission.UPDATE_METRIC,
            Permission.TRIGGER_SCAN);

        log.info("Data initialization completed successfully");
    }

    private User createUser(String email, String username, String phone) {
        User user = User.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode("123")) // All users get password "123"
                .phone(phone)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    private void createRegularUser(String email, String username, String phone, Permission... permissions) {
        User user = createUser(email, username, phone);
        permissionService.addUserPermissions(user.getId(), permissions);
        log.info("Created user: {} with permissions: {}", email, Arrays.toString(permissions));
    }
} 