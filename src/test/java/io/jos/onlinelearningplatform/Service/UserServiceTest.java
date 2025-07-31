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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

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
    }

    @Test
    @DisplayName("Register - Fails with duplicate username")
    void register_DuplicateUsername_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.register("existingUser", "test@example.com", "password123", "STUDENT")
        );
        assertTrue(exception.getMessage().contains("already taken"));
    }

    @Test
    @DisplayName("Login - Success with valid credentials")
    void loginUser_ValidCredentials_Success() {
        // Arrange
        String username = "testUser";
        String password = "password123";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPasswordHash("hashedPassword");
        when(userRepository.findByUsername(eq(username))).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(eq(password), anyString())).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> userService.loginUser(username, password));

        // Assert
        assertTrue(mockUser.isConnected());
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("Login - Fails with invalid username")
    void loginUser_InvalidUsername_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.loginUser("nonexistent", "password123")
        );
        assertTrue(exception.getMessage().contains("Invalid username or password"));
    }

    @Test
    @DisplayName("Login - Fails with wrong password")
    void loginUser_WrongPassword_ThrowsException() {
        // Arrange
        User mockUser = new User();
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.loginUser("testUser", "wrongPassword")
        );
        assertTrue(exception.getMessage().contains("Invalid username or password"));
    }

    @Test
    @DisplayName("Logout - Success for logged in user")
    void logoutUser_LoggedInUser_Success() {
        // Arrange
        User mockUser = new User();
        mockUser.setConnected(true);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        // Act
        userService.logoutUser("testUser");

        // Assert
        assertFalse(mockUser.isConnected());
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("Logout - Fails with non-existent user")
    void logoutUser_NonexistentUser_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
            userService.logoutUser("nonexistent")
        );
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Change Password - Success with valid old password")
    void changePassword_ValidOldPassword_Success() {
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
    }

    @Test
    @DisplayName("Change Password - Fails with invalid old password")
    void changePassword_InvalidOldPassword_ThrowsException() {
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
    }

    @Test
    @DisplayName("View Profile - Success for existing user")
    void viewProfile_ExistingUser_Success() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("testUser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertDoesNotThrow(() -> userService.viewProfile(1L));
    }

    @Test
    @DisplayName("Delete User - Success for existing user")
    void deleteUser_ExistingUser_Success() {
        // Arrange
        User mockUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).delete(mockUser);
    }
}
