package com.videoclub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.videoclub.config.TestSecurityConfig;
import com.videoclub.entity.User;
import com.videoclub.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User user;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(User.Role.USER);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        UserDetails result = authService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_WhenAdminUser_ShouldReturnUserDetailsWithAdminRole() {
        // Given
        user.setRole(User.Role.ADMIN);
        when(userRepository.findByUsername("adminuser")).thenReturn(Optional.of(user));

        // When
        UserDetails result = authService.loadUserByUsername("adminuser");

        // Then
        assertNotNull(result);
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        verify(userRepository, times(1)).findByUsername("adminuser");
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class, 
                () -> authService.loadUserByUsername("nonexistent")
        );
        
        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = authService.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals(User.Role.USER, result.get().getRole());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = authService.findByUsername("nonexistent");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void createUser_ShouldEncryptPasswordAndSaveUser() {
        // Given
        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");
        savedUser.setRole(User.Role.USER);
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = authService.createUser("newuser", "new@example.com", "plainpassword", User.Role.USER);

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(User.Role.USER, result.getRole());
        
        // Verify that save was called with encrypted password
        verify(userRepository, times(1)).save(argThat(savedUserArg -> {
            assertNotEquals("plainpassword", savedUserArg.getPassword()); // Password should be encrypted
            assertTrue(savedUserArg.getPassword().startsWith("$2")); // BCrypt hash starts with $2
            return true;
        }));
    }

    @Test
    void createUser_WithAdminRole_ShouldCreateAdminUser() {
        // Given
        User savedAdminUser = new User();
        savedAdminUser.setId(3L);
        savedAdminUser.setUsername("admin");
        savedAdminUser.setEmail("admin@example.com");
        savedAdminUser.setRole(User.Role.ADMIN);
        
        when(userRepository.save(any(User.class))).thenReturn(savedAdminUser);

        // When
        User result = authService.createUser("admin", "admin@example.com", "adminpass", User.Role.ADMIN);

        // Then
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals(User.Role.ADMIN, result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_ShouldSaveAndReturnUser() {
        // Given
        when(userRepository.save(user)).thenReturn(user);

        // When
        User result = authService.saveUser(user);

        // Then
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserPassword_ShouldEncryptAndSaveNewPassword() {
        // Given
        String originalPassword = user.getPassword();
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = authService.updateUserPassword(user, "newpassword123");

        // Then
        assertNotNull(result);
        assertNotEquals(originalPassword, user.getPassword()); // Password should have changed
        assertNotEquals("newpassword123", user.getPassword()); // Should be encrypted, not plain text
        assertTrue(user.getPassword().startsWith("$2")); // BCrypt hash format
        
        // Verify new password can be validated
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("newpassword123", user.getPassword()));
        
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserPassword_WithEmptyPassword_ShouldStillEncrypt() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = authService.updateUserPassword(user, "");

        // Then
        assertNotNull(result);
        assertNotEquals("", user.getPassword()); // Should be encrypted, not empty
        assertTrue(user.getPassword().startsWith("$2")); // BCrypt hash format
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserPassword_WithNullUser_ShouldHandleGracefully() {
        // Given
        User nullUser = null;

        // When & Then
        assertThrows(NullPointerException.class, 
                () -> authService.updateUserPassword(nullUser, "newpassword"));
        
        verify(userRepository, never()).save(any());
    }
}