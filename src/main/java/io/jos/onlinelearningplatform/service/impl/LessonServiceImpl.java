package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.service.LessonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LessonServiceImpl implements LessonService {
    private static final Logger logger = LoggerFactory.getLogger(LessonServiceImpl.class);
    private final LessonRepository lessonRepository;

    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }
    
    @Override
    public void createLesson(Student student, Teacher teacher, String lessonTitle, String lessonContent) {
        logger.info("Creating lesson '{}' by teacher {} for student {}",
            lessonTitle, teacher.getUsername(), student.getUsername());
        // ... existing lesson creation logic ...
        logger.debug("Lesson content length: {} characters", lessonContent.length());
    }


    @Override
    public void viewLessonDetails(Long lessonId) {
        logger.info("Viewing details for lesson with ID: {}", lessonId);
        // ... existing view logic ...
    }
}
