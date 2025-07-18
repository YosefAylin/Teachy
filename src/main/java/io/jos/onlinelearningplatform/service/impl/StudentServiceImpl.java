package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.StudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    private final UserRepository userRepository;
    public StudentServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void enrollInCourse(Long userId, Long courseId) {

    }

    @Override
    public void dropCourse(Long userId, Long courseId) {

    }

    @Override
    public void viewEnrolledCourses(Long userId) {

    }

    @Override
    public void submitAssignment(Long userId, Long courseId, String assignmentContent) {

    }

    @Override
    public void viewAssignments(Long userId, Long courseId) {

    }

    @Override
    public void viewGrades(Long userId, Long courseId) {

    }

    @Override
    public void participateInDiscussion(Long userId, Long courseId, String discussionContent) {

    }

    @Override
    public void viewDiscussions(Long userId, Long courseId) {

    }
}
