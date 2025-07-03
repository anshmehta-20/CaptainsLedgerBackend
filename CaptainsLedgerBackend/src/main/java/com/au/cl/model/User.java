// src/main/java/com/au/cl/model/User.java

package com.au.cl.model;

import jakarta.persistence.*; // Use jakarta.persistence for Spring Boot 3+
import lombok.Data; // From Lombok dependency
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Entity // Marks this class as a JPA entity, mapping to a database table
@Table(name = "users") // Specifies the table name in the database (optional, defaults to class name)
public class User {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments the ID
    private Long id;

    @Column(unique = true, nullable = false) // Ensures username is unique and not null
    private String username;

    @Column(nullable = false)
    private String password; // Store hashed passwords, not plain text!

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING) // Stores the enum name as a string in the DB
    @Column(nullable = false)
    private Role role; // ADMIN or AVENGER

    private double balance; // For payments

    // You can add more fields here later as per your requirements
    // e.g., String firstName, String lastName, String avatarUrl, boolean isAlive, etc.
}