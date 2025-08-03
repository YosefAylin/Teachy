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
    public void removeLesson(Long lessonId) {
        logger.info("Removing lesson with ID: {}", lessonId);
        // ... existing removal logic ...
        if (lessonRepository.existsById(lessonId)) {
            lessonRepository.deleteById(lessonId);
            logger.debug("Lesson with ID {} successfully removed", lessonId);
        } else {
            logger.warn("Attempted to remove non-existent lesson with ID: {}", lessonId);
        }

    }

    @Override
    public void updateLesson(Long lessonId, String newTitle, String newContent) {
        logger.info("Updating lesson with ID: {}", lessonId);
        // ... existing update logic ...
        if (lessonRepository.existsById(lessonId)) {
            // Assuming a method to find and update the lesson exists

            logger.debug("Lesson with ID {} successfully updated to title '{}'", lessonId, newTitle);
        } else {
            logger.warn("Attempted to update non-existent lesson with ID: {}", lessonId);
        }

    }


    @Override
    public void viewLessonDetails(Long lessonId) {
        logger.info("Viewing details for lesson with ID: {}", lessonId);
        // ... existing view logic ...
    }
}
