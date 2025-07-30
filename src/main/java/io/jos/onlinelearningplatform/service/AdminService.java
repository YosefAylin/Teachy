package io.jos.onlinelearningplatform.service;

import org.springframework.stereotype.Service;

@Service
public interface AdminService {
    void manageUsers();
    void viewFeedback();

    void registerUser(String adminUsername, String adminPassword, String adminEmail);
}
