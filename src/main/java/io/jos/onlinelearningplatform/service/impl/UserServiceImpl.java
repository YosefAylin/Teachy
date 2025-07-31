package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Admin;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserService interface that provides user management functionality.
 * This service handles user registration, authentication, and profile management operations.
 * All methods are transactional to ensure data consistency.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserServiceImpl with the required dependencies.
     *
     * @param userRepository Repository for user data access
     * @param passwordEncoder Encoder for securely hashing passwords
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user in the system with the specified role.
     * The password is securely hashed before storage.
     *
     * @param username The unique username for the new user
     * @param email The email address of the user
     * @param rawPassword The plain text password (will be hashed)
     * @param role The role of the user (ADMIN, TEACHER, or STUDENT)
     * @return The newly created User entity
     * @throws IllegalArgumentException if the username is already taken
     */
    public User register(String username,
                         String email,
                         String rawPassword,
                         String role) {
        String hash = passwordEncoder.encode(rawPassword);
        User u;
        switch(role.toUpperCase()) {
            case "ADMIN":
                u = new Admin(username, email, hash);
                break;
            case "TEACHER":
                u = new Teacher(username, email, hash);
                break;
            case "STUDENT":
            default:
                u = new Student(username, email, hash);
                break;
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken");
        }
        return userRepository.save(u);
    }

    /**
     * Authenticates a user with the provided credentials and marks them as connected.
     * 
     * @param username The username of the user to authenticate
     * @param password The plain text password to verify
     * @throws IllegalArgumentException if the username doesn't exist or the password is incorrect
     */
    @Override
    public void loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        user.setConnected(true);
        userRepository.save(user);  // Explicitly save the updated connection status
        logger.info("Login successful for user: {}", username);
    }

    /**
     * Logs out a user by marking them as disconnected.
     * 
     * @param username The username of the user to log out
     * @throws RuntimeException if the user doesn't exist
     * @throws IllegalArgumentException if the user is not currently logged in
     */
    @Override
    public void logoutUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (!user.isConnected()) {
            throw new IllegalArgumentException("User '" + username + "' is not currently logged in");
        }
        user.setConnected(false);
        userRepository.save(user);  // Explicitly save the updated connection status
        logger.info("User '{}' has been logged out", username);
    }

    /**
     * Permanently deletes a user from the system.
     * 
     * @param userId The ID of the user to delete
     * @throws RuntimeException if the user doesn't exist
     */
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        logger.info("User with ID {} has been deleted", userId);
    }

    /**
     * Retrieves and displays a user's profile information.
     * 
     * @param userId The ID of the user whose profile to view
     * @throws RuntimeException if the user doesn't exist
     */
    @Override
    public void viewProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        logger.debug("User Profile: {}", user);
    }

    /**
     * Changes a user's password after verifying the old password.
     * 
     * @param userId The ID of the user whose password to change
     * @param oldPassword The current password for verification
     * @param newPassword The new password to set
     * @throws RuntimeException if the user doesn't exist or the old password is incorrect
     */
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            logger.info("Password changed successfully for user: {}", user.getUsername());
        } else {
            throw new RuntimeException("Old password is incorrect");
        }
    }
}
