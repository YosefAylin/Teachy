package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.User;

public interface StudentService {
    // void registerUser(String username, String rawPassword, String email);
    // void findTeacherBySubject(String subject);
    void bookTeacher(String teacherId, String dateTime);
    void cancelBooking(String lessonId);
    // void makePayment(String lessonId, String paymentMethod);
}

