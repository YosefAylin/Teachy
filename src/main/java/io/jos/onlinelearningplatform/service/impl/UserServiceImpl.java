package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(String username, String password, String email) {

    }

    @Override
    public void loginUser(String username, String password) {

    }

    @Override
    public void logoutUser(Long userId) {

    }

    @Override
    public void updateProfile(Long userId, String newUsername, String newEmail) {

    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public void viewProfile(Long userId) {

    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {

    }
}
