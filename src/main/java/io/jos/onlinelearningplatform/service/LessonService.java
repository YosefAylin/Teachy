package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonService {
//    void createLesson(Student s, Teacher t, String lessonTitle, String lessonContent);
//    void removeLesson(Long lessonId);
//    void updateLesson(Long lessonId, String newTitle, String newContent);
//    void viewLessonDetails(Long lessonId);
//    List<Lesson> getAllForStudent(Long studentId);
    List<Lesson> getUpcomingForStudent(Long studentId);
    List<Lesson> getPastForStudent(Long studentId);

    void requestLesson(Long currentStudentId, Long teacherId, Long courseId, LocalDateTime scheduledAt, String note);
}
