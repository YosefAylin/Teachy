// src/main/java/io/jos/onlinelearningplatform/service/impl/LessonServiceImpl.java
package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.CourseRepository;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.LessonService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class LessonServiceImpl implements LessonService {

    private static final Logger logger = LoggerFactory.getLogger(LessonServiceImpl.class);

    private final LessonRepository lessonRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;

    public LessonServiceImpl(LessonRepository lessonRepo, UserRepository userRepo, CourseRepository courseRepo) {
        this.lessonRepo = lessonRepo;
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
    }
    @Override
    @Transactional
    public Lesson requestLesson(Long studentId, Long teacherId, Long courseId, LocalDateTime timestamp) {
        logger.debug("Requesting lesson - Student ID: {}, Teacher ID: {}, Course ID: {}, Timestamp: {}",
                    studentId, teacherId, courseId, timestamp);

        Student student = (Student) userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        Teacher teacher = (Teacher) userRepo.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (timestamp == null || timestamp.toLocalDate ().isBefore(java.time.LocalDate.now())) {
            logger.warn("Invalid timestamp for lesson request: {}", timestamp);
            throw new IllegalArgumentException("Scheduled date must be today or in the future.");
        }

        Lesson l = new Lesson();
        l.setStudent(student);
        l.setTeacher(teacher);
        l.setCourse(course);
        l.setTimestamp(timestamp);
        l.setStatus("PENDING");
        Lesson savedLesson = lessonRepo.save(l);

        logger.info("Successfully created lesson request with ID: {} for student: {} and teacher: {}",
                   savedLesson.getId(), studentId, teacherId);
        return savedLesson;
    }

    @Override
    public java.util.List<Lesson> getUpcomingForStudent(Long studentId) {
        logger.debug("Getting upcoming lessons for student ID: {}", studentId);
        java.util.List<String> statuses = java.util.Arrays.asList("PENDING", "ACCEPTED");
        java.time.LocalDateTime today = java.time.LocalDate.now().atStartOfDay();
        List<Lesson> lessons = lessonRepo.findByStudent_IdAndStatusInAndTimestampGreaterThanEqualOrderByTimestampAsc(studentId, statuses, today);
        logger.info("Found {} upcoming lessons for student ID: {}", lessons.size(), studentId);
        return lessons;
    }

    @Override
    public java.util.List<Lesson> getPastForStudent(Long studentId) {
        logger.debug("Getting past lessons for student ID: {}", studentId);
        LocalDateTime today = java.time.LocalDate.now().atStartOfDay();

        // accepted lessons that already happened
        java.util.List<String> acceptedOnly = java.util.Collections.singletonList("ACCEPTED");
        java.util.List<Lesson> acceptedPast =
                lessonRepo.findByStudent_IdAndStatusInAndTimestampLessThanOrderByTimestampDesc(
                        studentId, acceptedOnly, today
                );

        // any rejected lesson (any date)
        List<Lesson> rejectedAnyTimestamp =
                lessonRepo.findByStudent_IdAndStatusOrderByTimestampDesc(studentId, "REJECTED");

        List<Lesson> out = new ArrayList<>(acceptedPast);
        out.addAll(rejectedAnyTimestamp);
        out.sort(Comparator.comparing(Lesson::getTimestamp).reversed());

        logger.info("Found {} past lessons for student ID: {} ({} accepted past, {} rejected)",
                   out.size(), studentId, acceptedPast.size(), rejectedAnyTimestamp.size());
        return out;
    }

    @Override
    public List<Lesson> getAllLessonsForStudent(Long studentId) {
        logger.debug("Getting all lessons for student ID: {}", studentId);
        List<Lesson> lessons = lessonRepo.findByStudentIdAndTimestampBetweenOrderByTimestampAsc(
            studentId,
            LocalDateTime.of(2020, 1, 1, 0, 0),
            LocalDateTime.of(2030, 12, 31, 23, 59)
        );
        logger.info("Retrieved {} total lessons for student ID: {}", lessons.size(), studentId);
        return lessons;
    }
}
