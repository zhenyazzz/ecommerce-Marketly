package org.example.userservice.repository;

import org.example.userservice.model.Role;
import org.example.userservice.model.User;
import org.example.userservice.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> findByUsernameContainingOrEmailContainingIgnoreCase(@Param("search") String search, Pageable pageable);
    
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles")
    Page<User> findByRolesIn(@Param("roles") Set<Role> roles, Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.status = :status AND r IN :roles")
    Page<User> findByStatusAndRolesIn(@Param("status") UserStatus status, @Param("roles") Set<Role> roles, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findByStatusAndUsernameContainingOrEmailContainingIgnoreCase(@Param("status") UserStatus status, @Param("search") String search, Pageable pageable);
} 