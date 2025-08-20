package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Schedule;
import io.jos.onlinelearningplatform.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface StudentService {
    void findTeacherBySubject(String subject);
    void bookTeacher(String teacherId, String dateTime);
    void cancelBooking(String lessonId);
    void makePayment(String lessonId, String paymentMethod);
    List<Schedule> getStudentSchedule(Long studentId);
    List<Schedule> getUpcomingSchedule(Long studentId);
    List<Schedule> getSchedulesForMonth(Long studentId, LocalDateTime start, LocalDateTime end);

    Lesson getNextLesson(Long studentId);}

