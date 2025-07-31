package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl implements TeacherService {
    private static final Logger logger = LoggerFactory.getLogger(TeacherServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void handleBooking(Long lessonId) {
        logger.info("Teacher handling booking request for lesson ID: {}", lessonId);
        if (lessonId == null) {
            logger.warn("Attempted to handle booking with null lessonId");
            throw new IllegalArgumentException("Invalid lesson ID");
        }
        // Logic to accept a booking for a lesson
        // This would typically involve updating the Lesson object in the database
        // For now, we will just print a message
        logger.info("Accepting booking for lesson with ID: {}", lessonId);
        logger.info("Successfully handled booking for lesson {}", lessonId);
    }
}
