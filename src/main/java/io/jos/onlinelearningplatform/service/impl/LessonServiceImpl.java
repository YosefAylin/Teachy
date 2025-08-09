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
    public void requestLesson(Long studentId, Long teacherId, Long courseId,
                                java.time.LocalDateTime when, String noteUnused) {
        io.jos.onlinelearningplatform.model.Student student =
                (io.jos.onlinelearningplatform.model.Student) userRepo.findById(studentId)
                        .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        io.jos.onlinelearningplatform.model.Teacher teacher =
                (io.jos.onlinelearningplatform.model.Teacher) userRepo.findById(teacherId)
                        .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));

        io.jos.onlinelearningplatform.model.Course course =
                courseRepo.findById(courseId)
                        .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (when == null || when.toLocalDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Scheduled date must be today or in the future.");
        }

        Lesson l = new Lesson();
        l.setStudent(student);
        l.setTeacher(teacher);
        l.setCourse(course);
        l.setDate(when.toLocalDate());
        l.setStatus("PENDING");
        lessonRepo.save(l);
    }

    @Override
    public java.util.List<Lesson> getUpcomingForStudent(Long studentId) {
        java.util.List<String> statuses = java.util.Arrays.asList("PENDING", "ACCEPTED");
        java.time.LocalDate today = java.time.LocalDate.now();
        return lessonRepo.findByStudent_IdAndStatusInAndDateGreaterThanEqualOrderByDateAsc(
                studentId, statuses, today
        );
    }

    @Override
    public java.util.List<Lesson> getPastForStudent(Long studentId) {
        java.time.LocalDate today = java.time.LocalDate.now();

        // accepted lessons that already happened
        java.util.List<String> acceptedOnly = java.util.Collections.singletonList("ACCEPTED");
        java.util.List<Lesson> acceptedPast =
                lessonRepo.findByStudent_IdAndStatusInAndDateLessThanOrderByDateDesc(
                        studentId, acceptedOnly, today
                );

        // any rejected lesson (any date)
        java.util.List<Lesson> rejectedAnyDate =
                lessonRepo.findByStudent_IdAndStatusOrderByDateDesc(studentId, "REJECTED");

        java.util.ArrayList<Lesson> out = new java.util.ArrayList<>(acceptedPast);
        out.addAll(rejectedAnyDate);
        out.sort(java.util.Comparator.comparing(Lesson::getDate).reversed());
        return out;
    }

}

