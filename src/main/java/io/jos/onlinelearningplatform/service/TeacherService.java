package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.Course;
import io.jos.onlinelearningplatform.model.Lesson;

import java.util.List;

public interface TeacherService {
    void handleBooking(Long lessonId);
    void addCourse(Long teacherId, Long courseId);
    List<Course> getTeacherCourses(Long teacherId);
    List<Lesson> getUpcomingLessons(Long teacherId);
}
