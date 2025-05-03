package org.example.securityservice.repository;

import org.example.securityservice.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    Optional<UserPermission> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_permission (user_id, permission_ids) VALUES (?1, ?2) " +
            "ON CONFLICT (user_id) DO UPDATE SET permission_ids = user_permission.permission_ids || ?2", 
            nativeQuery = true)
    void addUserPermission(Long userId, Integer[] permissionIds);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_permission (user_id, permission_ids) VALUES (?1, ?2) " +
            "ON CONFLICT (user_id) DO UPDATE SET permission_ids = ?2", 
            nativeQuery = true)
    void setUserPermissions(Long userId, Integer[] permissionIds);
} 