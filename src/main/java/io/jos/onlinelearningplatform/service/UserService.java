package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.User;

public interface UserService {
    User register(String username, String email, String rawPassword, String role);
    void deleteUser(Long userId);
    void viewProfile(Long userId);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
