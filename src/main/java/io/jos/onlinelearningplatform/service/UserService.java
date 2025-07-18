package io.jos.onlinelearningplatform.service;

public interface UserService {
    void registerUser(String username, String password, String email);
    void loginUser(String username, String password);
    void logoutUser(Long userId);
    void updateProfile(Long userId, String newUsername, String newEmail);
    void deleteUser(Long userId);
    void viewProfile(Long userId);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
