package io.jos.onlinelearningplatform.service;

import org.springframework.stereotype.Service;

@Service
public interface AdminService {
    void addCourse(String courseName, String description);
    void removeCourse(Long courseId);
    void updateCourse(Long courseId, String courseName, String description);
    void viewAllCourses();
    void manageUsers();
    void viewFeedback();
}
