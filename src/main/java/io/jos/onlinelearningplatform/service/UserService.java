package io.jos.onlinelearningplatform.service;

public interface UserService {
    void loginUser(String username, String password);
    void logoutUser(String username);
    void deleteUser(Long userId);
    void viewProfile(Long userId);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
