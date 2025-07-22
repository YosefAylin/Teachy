package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }
    
    @Override
    public void createLesson(Student s, Teacher t, String lessonTitle, String lessonContent) {

    }

    @Override
    public void removeLesson(Long lessonId) {

    }

    @Override
    public void updateLesson(Long lessonId, String newTitle, String newContent) {

    }


    @Override
    public void viewLessonDetails(Long lessonId) {

    }
}
