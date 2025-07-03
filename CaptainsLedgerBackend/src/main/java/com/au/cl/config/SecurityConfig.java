package com.au.cl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // You would typically inject your JwtRequestFilter here if you uncomment it later
    // private final JwtRequestFilter jwtRequestFilter;
    // public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
    //     this.jwtRequestFilter = jwtRequestFilter;
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless REST APIs
            .cors(cors -> {}) // Enable CORS (ensure you have a CorsConfigurationSource bean if needed)
            .authorizeHttpRequests(authorize -> authorize
                // Permit specific API endpoints for authentication/public access
                // Use /** for paths and anything under them
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/api/auth/**"), // Matches /api/auth/login, /api/auth/register, etc.
                    AntPathRequestMatcher.antMatcher("/api/public/**"), // Example for other public APIs
                    AntPathRequestMatcher.antMatcher("/api/some-other-public-endpoint") // Exact endpoint if needed
                ).permitAll()
                // All other requests REQUIRE authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless sessions (for JWT)
            );
            // Uncomment and add your JWT filter when ready
            // .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // THIS IS THE METHOD I WAS REFERRING TO AS "webSecurityCustomizer"
    // It configures WebSecurity to ignore specific paths from the security filter chain entirely.
    // This is primarily for STATIC RESOURCES that Spring Security should not touch.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            // Explicit HTML files that are directly served
            AntPathRequestMatcher.antMatcher("/"),
            AntPathRequestMatcher.antMatcher("/index.html"),
            AntPathRequestMatcher.antMatcher("/register.html"),
            AntPathRequestMatcher.antMatcher("/dashboard.html"),

            // Corrected: Use /** to match all files and subdirectories within these folders
            AntPathRequestMatcher.antMatcher("/css/**"),
            AntPathRequestMatcher.antMatcher("/js/**"),
            AntPathRequestMatcher.antMatcher("/images/**"),
            AntPathRequestMatcher.antMatcher("/fonts/**"),
            AntPathRequestMatcher.antMatcher("/videos/**"),
            AntPathRequestMatcher.antMatcher("/favicon.ico"),

            // *** NEW ADDITIONS FOR DEBUGGING CSS/JS 403 ***
            // These will ignore ANY file ending with .css, .js, etc., regardless of its folder.
            // This is a powerful temporary rule to confirm if it's a path matching issue.
            AntPathRequestMatcher.antMatcher("/**/*.css"), // Matches any .css file (e.g., /myfolder/style.css)
            AntPathRequestMatcher.antMatcher("/**/*.js"),  // Matches any .js file
            AntPathRequestMatcher.antMatcher("/**/*.png"), // Matches any .png image
            AntPathRequestMatcher.antMatcher("/**/*.jpg"), // Matches any .jpg image
            AntPathRequestMatcher.antMatcher("/**/*.jpeg"),// Matches any .jpeg image
            AntPathRequestMatcher.antMatcher("/**/*.gif"), // Matches any .gif image
            AntPathRequestMatcher.antMatcher("/**/*.svg"), // Matches any .svg image
            AntPathRequestMatcher.antMatcher("/**/*.webp") // Matches any .webp image (common for modern images)
        );
    }

    // Defines a password encoder for hashing passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}