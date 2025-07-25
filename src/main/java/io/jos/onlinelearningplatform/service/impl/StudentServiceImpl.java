package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
        Student s = new Student();
        s.setUsername(username);
        s.setEmail(email);
        s.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(s);
    }

    @Override
    public void findTeacherBySubject(String subject) {

    }

    @Override
    public void bookTeacher(String teacherId, String dateTime) {

    }

    @Override
    public void cancelBooking(String lessonId) {

    }

}


