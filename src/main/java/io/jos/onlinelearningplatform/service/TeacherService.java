package io.jos.onlinelearningplatform.service;

public interface TeacherService {
    void registerUser(String username, String rawPassword, String email);
    void acceptBooking(Long lessonId);
    void rejectBooking(Long lessonId);
    void respondToFeedback(Long reviewId, String responseText);
}
