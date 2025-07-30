package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;

public interface LessonService {
    void createLesson(Student s, Teacher t, String lessonTitle, String lessonContent);
//    void removeLesson(Long lessonId);
//    void updateLesson(Long lessonId, String newTitle, String newContent);
    void viewLessonDetails(Long lessonId);
}
