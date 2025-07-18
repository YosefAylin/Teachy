package io.jos.onlinelearningplatform.service;

public interface StudentService {
    void enrollInCourse(Long userId, Long courseId);
    void dropCourse(Long userId, Long courseId);
    void viewEnrolledCourses(Long userId);
    void submitAssignment(Long userId, Long courseId, String assignmentContent);
    void viewAssignments(Long userId, Long courseId);
    void viewGrades(Long userId, Long courseId);
    void participateInDiscussion(Long userId, Long courseId, String discussionContent);
    void viewDiscussions(Long userId, Long courseId);
}
