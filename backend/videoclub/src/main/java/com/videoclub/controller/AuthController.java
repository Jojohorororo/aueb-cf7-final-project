package com.videoclub.controller;

import com.videoclub.dto.JwtResponse;
import com.videoclub.dto.LoginDto;
import com.videoclub.entity.User;
import com.videoclub.security.JwtUtils;
import com.videoclub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Authentication", description = "Authentication API")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "User login")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = authService.findByUsername(userDetails.getUsername()).orElse(null);

            String role = user != null ? user.getRole().name() : "USER";

            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), role));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> signUpRequest) {
        try {
            String username = signUpRequest.get("username");
            String email = signUpRequest.get("email");
            String password = signUpRequest.get("password");

            if (authService.findByUsername(username).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username is already taken!");
                return ResponseEntity.badRequest().body(error);
            }

            User user = authService.createUser(username, email, password, User.Role.USER);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get user profile")
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = authService.findByUsername(username).orElse(null);
            
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("role", user.getRole().name());
            profile.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch profile");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Update user profile")
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody Map<String, String> updateRequest, 
                                             Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = authService.findByUsername(username).orElse(null);
            
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.notFound().build();
            }

            // Update email if provided
            String newEmail = updateRequest.get("email");
            if (newEmail != null && !newEmail.trim().isEmpty()) {
                user.setEmail(newEmail.trim());
            }

            // Update password if provided
            String newPassword = updateRequest.get("password");
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user = authService.updateUserPassword(user, newPassword);
            } else {
                user = authService.saveUser(user);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}