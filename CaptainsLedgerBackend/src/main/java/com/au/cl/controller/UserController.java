// src/main/java/com/au/cl/controller/UserController.java

package com.au.cl.controller;

import com.au.cl.model.User;
import com.au.cl.dto.UserCreateRequest;
import com.au.cl.model.Role;
import com.au.cl.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired // Inject the PasswordEncoder
    private PasswordEncoder passwordEncoder;

//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        // --- IMPORTANT: Hash the password before saving! ---
//        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password
//
//        // Set a default role for testing if not provided (or based on business logic)
//        if (user.getRole() == null) {
//            user.setRole(Role.AVENGER); // Default to AVENGER if not specified
//        }
//        if (user.getBalance() < 0) {
//            user.setBalance(0.0);
//        }
//
//        User savedUser = userRepository.save(user);
//        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
//    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateRequest request) {
        // If validation fails (e.g., no email), Spring Boot automatically
        // returns a 400 Bad Request response before this method body is even executed.

        // Manually create and populate the User entity from the valid request
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set your business logic defaults
        newUser.setRole(Role.AVENGER);
        newUser.setBalance(0.0);

        User savedUser = userRepository.save(newUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}