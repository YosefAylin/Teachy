package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.*;
import java.util.List;

public interface AdminService {
    long getTotalStudents();
    long getTotalTeachers();
    long getTotalCourses();
    long getTotalLessons();
    List<User> getRecentUsers(int limit);
    List<User> getAllUsers();
    List<Course> getAllCourses();
    List<Lesson> getAllLessons();
    void createCourse(String name, String description);
    void deleteCourse(Long courseId);
    void toggleUserActive(Long userId);
}