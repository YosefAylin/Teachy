package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void findTeacherBySubject(String subject) {

    }

    @Override
    public void bookTeacher(String teacherId, String dateTime) {
        logger.info("Student attempting to book teacher ID: {} for {}", teacherId, dateTime);
        if (teacherId == null || teacherId.isBlank() || dateTime == null || dateTime.isBlank()) {
            logger.warn("Invalid booking attempt with teacherId: {} and dateTime: {}", teacherId, dateTime);
            throw new IllegalArgumentException("Invalid booking data");
        }
        // Logic to book a teacher for a lesson at the specified date and time
        // This would typically involve creating a Lesson object and saving it to the database
        // For now, we will just print a message
        logger.info("Successfully booked teacher {} for {}", teacherId, dateTime);
    }

    @Override
    public void cancelBooking(String lessonId) {
        logger.info("Student attempting to cancel booking ID: {}", lessonId);
        if (lessonId == null || lessonId.isBlank()) {
            logger.warn("Invalid cancellation attempt with lessonId: {}", lessonId);
            throw new IllegalArgumentException("Invalid lesson ID");
        }
        // Logic to cancel a booking for a lesson
        // This would typically involve finding the Lesson object by ID and removing it from the database
        // For now, we will just print a message
        logger.info("Successfully cancelled booking {}", lessonId);
    }

    @Override
    public void makePayment(String lessonId, String paymentMethod) {

    }
}
