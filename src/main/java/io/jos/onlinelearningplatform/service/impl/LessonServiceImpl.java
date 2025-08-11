// src/main/java/io/jos/onlinelearningplatform/service/impl/LessonServiceImpl.java
package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.CourseRepository;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.LessonService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class LessonServiceImpl implements LessonService {

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
        Student student = (Student) userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        Teacher teacher = (Teacher) userRepo.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (timestamp == null || timestamp.toLocalDate ().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Scheduled date must be today or in the future.");
        }

        Lesson l = new Lesson();
        l.setStudent(student);
        l.setTeacher(teacher);
        l.setCourse(course);
        l.setTimestamp(timestamp);
        l.setStatus("PENDING");
        return lessonRepo.save(l);
    }

    @Override
    public java.util.List<Lesson> getUpcomingForStudent(Long studentId) {
        java.util.List<String> statuses = java.util.Arrays.asList("PENDING", "ACCEPTED");
        java.time.LocalDateTime today = java.time.LocalDate.now().atStartOfDay();
        return lessonRepo.findByStudent_IdAndStatusInAndTimestampGreaterThanEqualOrderByTimestampAsc(studentId, statuses, today);
    }

    @Override
    public java.util.List<Lesson> getPastForStudent(Long studentId) {
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
        return out;
    }



}

