package org.example.usersservice.repo;

import org.example.usersservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByEmailOrUsername(String email, String username);
    Optional<User> findByEmailAndUsername(String email, String username);
    Optional<User> findByIdAndEmail(Long id, String email);
    Optional<User> findByIdAndUsername(Long id, String username);
    Optional<User> findByIdAndEmailOrUsername(Long id, String email, String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
} 