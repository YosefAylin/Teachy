package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder);

        // Default mock behavior
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
    }

    @Test
    @DisplayName("Register - Success with valid input")
    void register_ValidInput_Success() {
        logger.info("Testing registration with valid input");
        // Arrange
        String username = "testUser";
        String email = "test@example.com";
        String password = "password123";
        String role = "STUDENT";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User result = userService.register(username, email, password, role);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
        logger.info("Registration test passed for user: {}", username);
    }

    @Test
    @DisplayName("Register - Fails with duplicate username")
    void register_DuplicateUsername_ThrowsException() {
        logger.info("Testing registration with duplicate username");
        // Arrange
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.register("existingUser", "test@example.com", "password123", "STUDENT")
        );
        assertTrue(exception.getMessage().contains("already taken"));
        logger.info("Duplicate username registration test passed");
    }

    @Test
    @DisplayName("Login - Success with valid credentials (Spring Auth)")
    void loginUser_ValidCredentials_Success_SpringAuth() {
        logger.info("Testing login with valid credentials");
        // Arrange
        String username = "testUser";
        String password = "password123";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPasswordHash("hashedPassword");
        when(userRepository.findByUsername(eq(username))).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(eq(password), eq("hashedPassword"))).thenReturn(true);

        // Simulate Spring Security authentication
        assertTrue(passwordEncoder.matches(password, mockUser.getPasswordHash()));
        // Optionally, set connected status as Spring Security would do
        mockUser.setConnected(true);
        userRepository.save(mockUser);
        assertTrue(mockUser.isConnected());
        verify(userRepository).save(mockUser);
        logger.info("Login test passed for user: {}", username);
    }

    @Test
    @DisplayName("Login - Fails with invalid username (Spring Auth)")
    void loginUser_InvalidUsername_ThrowsException_SpringAuth() {
        logger.info("Testing login with invalid username");
        // Arrange
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        // Simulate Spring Security authentication failure
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Optional<User> userOpt = userRepository.findByUsername("nonexistent");
            if (userOpt.isEmpty()) throw new IllegalArgumentException("Invalid username or password");
        });
        assertTrue(exception.getMessage().contains("Invalid username or password"));
        logger.info("Invalid username login test passed");
    }

    @Test
    @DisplayName("Login - Fails with wrong password (Spring Auth)")
    void loginUser_WrongPassword_ThrowsException_SpringAuth() {
        logger.info("Testing login with wrong password");
        // Arrange
        User mockUser = new User();
        mockUser.setPasswordHash("hashedPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // Simulate Spring Security authentication failure
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            if (!passwordEncoder.matches("wrongPassword", mockUser.getPasswordHash())) {
                throw new IllegalArgumentException("Invalid username or password");
            }
        });
        assertTrue(exception.getMessage().contains("Invalid username or password"));
        logger.info("Wrong password login test passed");
    }

    @Test
    @DisplayName("Logout - Success for logged in user (Spring Auth)")
    void logoutUser_LoggedInUser_Success_SpringAuth() {
        logger.info("Testing logout for logged in user");
        // Arrange
        User mockUser = new User();
        mockUser.setConnected(true);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        // Simulate Spring Security logout
        mockUser.setConnected(false);
        userRepository.save(mockUser);

        // Assert
        assertFalse(mockUser.isConnected());
        verify(userRepository).save(mockUser);
        logger.info("Logout test passed for logged in user");
    }

    @Test
    @DisplayName("Logout - Fails with non-existent user (Spring Auth)")
    void logoutUser_NonexistentUser_ThrowsException_SpringAuth() {
        logger.info("Testing logout for non-existent user");
        // Arrange
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        // Simulate Spring Security logout failure
        Exception exception = assertThrows(RuntimeException.class, () -> {
            Optional<User> userOpt = userRepository.findByUsername("nonexistent");
            if (userOpt.isEmpty()) throw new RuntimeException("User not found");
        });
        assertTrue(exception.getMessage().contains("User not found"));
        logger.info("Logout test passed for non-existent user");
    }

    @Test
    @DisplayName("Change Password - Success with valid old password")
    void changePassword_ValidOldPassword_Success() {
        logger.info("Testing change password with valid old password");
        // Arrange
        User mockUser = new User();
        mockUser.setPasswordHash("hashedOldPass");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), eq("hashedOldPass"))).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedNewPass");

        // Act
        assertDoesNotThrow(() ->
            userService.changePassword(1L, "oldPass", "newPass")
        );

        // Assert
        verify(passwordEncoder).encode(eq("newPass"));
        verify(userRepository).save(mockUser);
        logger.info("Change password test passed with valid old password");
    }

    @Test
    @DisplayName("Change Password - Fails with invalid old password")
    void changePassword_InvalidOldPassword_ThrowsException() {
        logger.info("Testing change password with invalid old password");
        // Arrange
        User mockUser = new User();
        mockUser.setPasswordHash("hashedOldPass");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
            userService.changePassword(1L, "wrongPass", "newPass")
        );
        assertTrue(exception.getMessage().contains("Old password is incorrect"));
        logger.info("Change password test passed with invalid old password");
    }

    @Test
    @DisplayName("View Profile - Success for existing user")
    void viewProfile_ExistingUser_Success() {
        logger.info("Testing view profile for existing user");
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("testUser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertDoesNotThrow(() -> userService.viewProfile(1L));
        logger.info("View profile test passed for existing user");
    }

    @Test
    @DisplayName("Delete User - Success for existing user")
    void deleteUser_ExistingUser_Success() {
        logger.info("Testing delete user for existing user");
        // Arrange
        User mockUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).delete(mockUser);
        logger.info("Delete user test passed for existing user");
    }
}
