package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TeacherServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(String username, String rawPassword, String email) {
        if (username == null || username.isBlank() ||
                rawPassword == null || rawPassword.length() < 6 ||
                email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid registration data");
        }
        Teacher t = new Teacher();
        t.setUsername(username);
        t.setEmail(email);
        t.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(t);
    }

    @Override
    public void acceptBooking(Long lessonId) {

    }

    @Override
    public void rejectBooking(Long lessonId) {

    }

    @Override
    public void respondToFeedback(Long feedbackId, String responseText) {

    }
}
