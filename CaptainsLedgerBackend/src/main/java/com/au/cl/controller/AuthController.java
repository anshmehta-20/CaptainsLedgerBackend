// src/main/java/com/au/cl/controller/AuthController.java

package com.au.cl.controller;

import com.au.cl.model.AuthRequest;
import com.au.cl.model.AuthResponse;
import com.au.cl.model.User;
import com.au.cl.model.Role; // Import Role
import com.au.cl.repository.UserRepository; // Import UserRepository
import com.au.cl.service.UserDetailsServiceImpl;
import com.au.cl.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.web.bind.annotation.*;

import java.util.Optional; // Import Optional

@RestController
@RequestMapping("/api/auth") // Base path for authentication endpoints
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository; // To get full User object for response

    @Autowired
    private PasswordEncoder passwordEncoder; // For hashing new user passwords if creating through auth

    // Register a new user (can be an alternative to /api/users POST)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest authRequest) {
        if (userRepository.existsByUsername(authRequest.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
        newUser.setUsername(authRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(authRequest.getPassword())); // Hash the password!
        newUser.setEmail(authRequest.getUsername() + "@example.com"); // Placeholder email, adjust later
        newUser.setRole(Role.AVENGER); // Default role for registration

        User savedUser = userRepository.save(newUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        // Retrieve the full User object to send in the response
        Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername());
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found after successful authentication."));


        return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getUsername(), user.getRole()));
    }
}