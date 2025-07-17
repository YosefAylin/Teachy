package jos.onlinelearningplatform.service;

public interface AdminService {
    void addCourse(String courseName, String description);
    void removeCourse(Long courseId);
    void updateCourse(Long courseId, String courseName, String description);
    void viewAllCourses();
    void manageUsers();
    void viewFeedback();
}
