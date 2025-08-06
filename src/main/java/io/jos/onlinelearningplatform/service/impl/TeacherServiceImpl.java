package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.CourseRepository;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.TeacherCourseRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TeacherServiceImpl implements TeacherService {
    private static final Logger logger = LoggerFactory.getLogger(TeacherServiceImpl.class);
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final TeacherCourseRepository teacherCourseRepository;

    public TeacherServiceImpl(UserRepository userRepository, CourseRepository courseRepository, LessonRepository lessonRepository, PasswordEncoder passwordEncoder, TeacherCourseRepository teacherCourseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.teacherCourseRepository = teacherCourseRepository;
    }

    @Override
    public void handleBooking(Long lessonId) {
        logger.info("Teacher handling booking request for lesson ID: {}", lessonId);
        if (lessonId == null) {
            logger.warn("Attempted to handle booking with null lessonId");
            throw new IllegalArgumentException("Invalid lesson ID");
        }

        logger.info("Successfully handled booking for lesson {}", lessonId);
    }



    @Override
    public List<Lesson> getUpcomingLessons(Long teacherId) {
        logger.info("Fetching upcoming lessons for teacher ID: {}", teacherId);

        if (teacherId == null) {
            logger.warn("Attempted to get lessons with null teacherId");
            throw new IllegalArgumentException("Invalid teacher ID");
        }

        // Get teacher's courses
        List<Course> teacherCourses = getTeachableCourses(teacherId);

        // Get upcoming lessons for all teacher's courses
        List<Lesson> upcomingLessons = lessonRepository.findUpcomingLessonsByCourseIds(
                teacherCourses.stream()
                        .map(Course::getId)
                        .collect(Collectors.toList()),
                LocalDateTime.now()
        );

        logger.info("Found {} upcoming lessons for teacher ID: {}", upcomingLessons.size(), teacherId);
        return upcomingLessons;
    }

    @Override
    public List<Student> getStudents(Long teacherId) {
        logger.info("Fetching students for teacher ID: {}", teacherId);

        if (teacherId == null) {
            logger.warn("Attempted to get students with null teacherId");
            throw new IllegalArgumentException("Invalid teacher ID");
        }

        // Get lessons for this teacher
        List<Lesson> teacherLessons = lessonRepository.findByTeacherId(teacherId);

        // Extract unique students from one-on-one lessons
        List<Student> enrolledStudents = teacherLessons.stream()
                .map(Lesson::getStudent)
                .distinct()
                .collect(Collectors.toList());

        logger.info("Found {} unique students for teacher ID: {}", enrolledStudents.size(), teacherId);
        return enrolledStudents;
    }

    @Override
    public Long getTeacherIdByUsername(String teacherName) {
        logger.info("Finding teacher ID for username: {}", teacherName);

        if (teacherName == null || teacherName.trim().isEmpty()) {
            logger.warn("Attempted to get teacher ID with null or empty username");
            throw new IllegalArgumentException("Invalid username");
        }

        Teacher teacher = (Teacher) userRepository.findByUsername(teacherName)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with username: " + teacherName));

        logger.info("Found teacher ID: {} for username: {}", teacher.getId(), teacherName);
        return teacher.getId();
    }

    @Override
    public List<Course> getAvailableCourses() {
        return courseRepository.findAll();
    }

    @Override
    @Transactional
    public void addTeachableCourse(Long teacherId, Long courseId) {
        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (teacherCourseRepository.existsByTeacherIdAndCourseId(teacherId, courseId)) {
            throw new IllegalStateException("Course already added for this teacher");
        }

        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setTeacher(teacher);
        teacherCourse.setCourse(course);
        teacherCourseRepository.save(teacherCourse);
    }

    @Override
    @Transactional
    public void removeTeachableCourse(Long teacherId, Long courseId) {
        teacherCourseRepository.deleteByTeacherIdAndCourseId(teacherId, courseId);
    }

    @Override
    public List<Course> getTeachableCourses(Long teacherId) {
        return teacherCourseRepository.findByTeacherId(teacherId).stream()
                .map(TeacherCourse::getCourse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lesson> getAcceptedLessons(Long teacherId) {
        return lessonRepository.findByTeacherIdAndStatus(teacherId, "ACCEPTED");

    }

    @Override
    public List<Lesson> getPendingLessons(Long teacherId) {
        return lessonRepository.findByTeacherIdAndStatus(teacherId,"PENDING");
    }

    @Override
    public int countPendingLessons(Long teacherId) {
        return lessonRepository.countByTeacherIdAndStatus(teacherId, "PENDING");
    }

    @Override
    public Teacher getTeacherProfile(Long teacherId) {
        return (Teacher) userRepository.findById(teacherId).orElse(null);
    }
}

