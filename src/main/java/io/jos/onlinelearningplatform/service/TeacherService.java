package io.jos.onlinelearningplatform.service;

public interface TeacherService {
    void createCourse(String courseName, String description);
    void updateCourse(Long courseId, String courseName, String description);
    void deleteCourse(Long courseId);
    void viewCourses();
    void manageStudents(Long courseId);
    void gradeAssignments(Long courseId, Long studentId, String grade);
    void viewFeedbackForCourse(Long courseId);
    void respondToFeedback(Long feedbackId, String responseText);
}
