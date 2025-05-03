package org.example.securityservice.service;

import lombok.RequiredArgsConstructor;
import org.example.securityservice.model.Permission;
import org.example.securityservice.model.UserPermission;
import org.example.securityservice.repository.UserPermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final UserPermissionRepository userPermissionRepository;

    public List<String> getUserPermissions(Long userId) {
        return userPermissionRepository.findByUserId(userId)
                .map(up -> Arrays.stream(up.getPermissionIds())
                        .map(Permission::fromId)
                        .map(Permission::getPermission)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public String getUserPermissionsAsString(Long userId) {
        return String.join(",", getUserPermissions(userId));
    }

    @Transactional
    public void addUserPermission(Long userId, Permission permission) {
        Integer[] permissionIds = {permission.getId()};
        userPermissionRepository.addUserPermission(userId, permissionIds);
    }

    @Transactional
    public void addUserPermissions(Long userId, Permission... permissions) {
        Integer[] permissionIds = Arrays.stream(permissions)
                .map(Permission::getId)
                .toArray(Integer[]::new);
        userPermissionRepository.addUserPermission(userId, permissionIds);
    }

    @Transactional
    public void setUserPermissions(Long userId, Permission... permissions) {
        Integer[] permissionIds = Arrays.stream(permissions)
                .map(Permission::getId)
                .toArray(Integer[]::new);
        userPermissionRepository.setUserPermissions(userId, permissionIds);
    }
} 