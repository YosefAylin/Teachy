package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final UserRepository userRepository;
    public TeacherServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createCourse(String courseName, String description) {

    }

    @Override
    public void updateCourse(Long courseId, String courseName, String description) {

    }

    @Override
    public void deleteCourse(Long courseId) {

    }

    @Override
    public void viewCourses() {

    }

    @Override
    public void manageStudents(Long courseId) {

    }

    @Override
    public void gradeAssignments(Long courseId, Long studentId, String grade) {

    }

    @Override
    public void viewFeedbackForCourse(Long courseId) {

    }

    @Override
    public void respondToFeedback(Long feedbackId, String responseText) {

    }
}
