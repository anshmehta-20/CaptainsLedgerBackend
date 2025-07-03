// src/main/java/com/au.cl/util/JwtUtil.java

package com.au.cl.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders; // Import this
import io.jsonwebtoken.security.Keys; // Import this
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component // Marks this as a Spring component
public class JwtUtil {

    // Secret key for signing the JWTs. Get this from application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Token expiration time in milliseconds (e.g., 24 hours)
    @Value("${jwt.expiration}")
    private long expiration; // milliseconds

    // Retrieve signing key from the secret string
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract a specific claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generate token for a user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // You can add custom claims here, e.g., user ID, roles
        claims.put("roles", userDetails.getAuthorities().stream()
                                .map(a -> a.getAuthority()).collect(Collectors.toList()));
        // Example: If you have userId in your UserDetails, add it here:
        // if (userDetails instanceof UserDetailsImpl) { // Assuming you create a custom UserDetailsImpl
        //      claims.put("userId", ((UserDetailsImpl) userDetails).getUserId());
        // }

        return createToken(claims, userDetails.getUsername());
    }

    // Create the token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Token validity
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate the token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}