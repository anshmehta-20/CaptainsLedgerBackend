// src/main/java/com/au/cl/repository/UserRepository.java

package com.au.cl.repository;

import com.au.cl.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA automatically provides methods like save(), findById(), findAll(), deleteById()

    // You can define custom query methods here by following naming conventions
    Optional<User> findByUsername(String username); // Finds a user by their username
    boolean existsByUsername(String username); // Checks if a user with given username exists
}