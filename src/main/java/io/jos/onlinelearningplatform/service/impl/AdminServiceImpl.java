package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Admin;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(String adminUsername, String rawPassword, String email) {
            if (adminUsername == null || adminUsername.isBlank() ||
                    rawPassword == null || rawPassword.length() < 6 ||
                    email == null || !email.contains("@")) {
                throw new IllegalArgumentException("Invalid registration data");
            }
            Admin a = new Admin();
            a.setUsername(adminUsername);
            a.setEmail(email);
            a.setPasswordHash(passwordEncoder.encode(rawPassword));
            userRepository.save(a);
    }
    @Override
    public void manageUsers() {

    }

    @Override
    public void viewFeedback() {

    }


}
