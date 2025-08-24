package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.Factory.UserFactory;
import io.jos.onlinelearningplatform.dto.RegisterDto;
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
    private final  UserFactory userFactory;

    /**
     * Constructs a new UserServiceImpl with the required dependencies.
     *
     * @param userRepository Repository for user data access
     * @param passwordEncoder Encoder for securely hashing passwords
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserFactory userFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFactory = userFactory;
    }



    public User register(RegisterDto dto) {
        User user = userFactory.createUser(dto);
        return userRepository.save(user);

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
    public User viewProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        logger.debug("User Profile: {}", user);
        return user;
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
    public Boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            logger.info("Password changed successfully for user: {}", user.getUsername());
            return true;
        } else {
            throw new RuntimeException("Old password is incorrect");
        }
    }
}
