package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.Role;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    @Transactional
    public void loginUser(String username, String password) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!passwordEncoder.matches(password, u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        u.setConnected(true);
        // no need to call save() if @Transactional—you’re modifying a managed entity
        System.out.println("Login successful for user: " + username);
    }

    @Override
    @Transactional
    public void logoutUser(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!u.isConnected()) {
            throw new IllegalArgumentException("User is not currently logged in");
        }
        u.setConnected(false);
        System.out.println("User '" + username + "' has been logged out.");
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        System.out.println("User with ID " + userId + " has been deleted.");

    }

    @Override
    public void viewProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("User Profile: " + user);

    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            System.out.println("Password changed successfully for user: " + user.getUsername());
        } else {
            throw new RuntimeException("Old password is incorrect");
        }

    }
}
