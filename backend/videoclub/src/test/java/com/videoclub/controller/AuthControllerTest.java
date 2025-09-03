package com.videoclub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoclub.config.TestSecurityConfig;
import com.videoclub.dto.LoginDto;
import com.videoclub.entity.User;
import com.videoclub.security.JwtUtils;
import com.videoclub.service.AuthService;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginDto loginDto;
    private User user;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(User.Role.USER);

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnJwtResponse() throws Exception {
        // Given
        String expectedToken = "jwt-token-123";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(expectedToken);
        when(authService.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(authService, times(1)).findByUsername("testuser");
    }

    @Test
    void authenticateUser_WithAdminUser_ShouldReturnJwtResponseWithAdminRole() throws Exception {
        // Given
        String expectedToken = "jwt-token-admin";
        user.setRole(User.Role.ADMIN);
        
        UserDetails adminUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("admin")
                .password("encodedPassword")
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();
        
        Authentication adminAuth = mock(Authentication.class);
        when(adminAuth.getPrincipal()).thenReturn(adminUserDetails);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(adminAuth);
        when(jwtUtils.generateJwtToken(adminAuth)).thenReturn(expectedToken);
        when(authService.findByUsername("admin")).thenReturn(Optional.of(user));

        LoginDto adminLogin = new LoginDto();
        adminLogin.setUsername("admin");
        adminLogin.setPassword("adminpass");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(authService, times(1)).findByUsername("admin");
    }

    @Test
    void authenticateUser_WithInvalidCredentials_ShouldReturnBadRequest() throws Exception {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateJwtToken(any());
        verify(authService, never()).findByUsername(anyString());
    }

    @Test
    void authenticateUser_WhenUserNotFoundInDatabase_ShouldReturnDefaultRole() throws Exception {
        // Given
        String expectedToken = "jwt-token-123";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(expectedToken);
        when(authService.findByUsername("testuser")).thenReturn(Optional.empty()); // User not found

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER")); // Default role

        verify(authService, times(1)).findByUsername("testuser");
    }

    @Test
    void registerUser_WithValidData_ShouldCreateUser() throws Exception {
        // Given
        Map<String, String> signUpRequest = new HashMap<>();
        signUpRequest.put("username", "newuser");
        signUpRequest.put("email", "new@example.com");
        signUpRequest.put("password", "newpassword");

        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setRole(User.Role.USER);

        when(authService.findByUsername("newuser")).thenReturn(Optional.empty());
        when(authService.createUser(anyString(), anyString(), anyString(), any(User.Role.class)))
                .thenReturn(newUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        verify(authService, times(1)).findByUsername("newuser");
        verify(authService, times(1)).createUser("newuser", "new@example.com", "newpassword", User.Role.USER);
    }

    @Test
    void registerUser_WithExistingUsername_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, String> signUpRequest = new HashMap<>();
        signUpRequest.put("username", "existinguser");
        signUpRequest.put("email", "existing@example.com");
        signUpRequest.put("password", "password");

        when(authService.findByUsername("existinguser")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username is already taken!"));

        verify(authService, times(1)).findByUsername("existinguser");
        verify(authService, never()).createUser(anyString(), anyString(), anyString(), any());
    }
}