package com.example.demo.repository;

import com.example.demo.entity.RoleType;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    // Find a user by email
   Optional< User >findByEmail(String email);
    Optional<User> findByName(String name);
    // Check if email already exists
    boolean existsByEmail(String email);
    boolean existsByMobileNo(String mobileNo);


    List<User> findAll();

    // ⭐ REQUIRED FOR ADMIN DASHBOARD (counts USER / DEALER / CUSTOMER / ADMIN)
    long countByRole(RoleType role);
}
