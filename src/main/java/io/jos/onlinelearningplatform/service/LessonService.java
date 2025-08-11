package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonService {
    Lesson requestLesson(Long studentId, Long teacherId, Long courseId, LocalDateTime timestamp);
    List<Lesson> getUpcomingForStudent(Long studentId);
    List<Lesson> getPastForStudent(Long studentId);
}
