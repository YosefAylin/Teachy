package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Course;
import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.repository.CourseRepository;
import io.jos.onlinelearningplatform.repository.LessonRepository;
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

    public TeacherServiceImpl(UserRepository userRepository, CourseRepository courseRepository, LessonRepository lessonRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
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
    @Transactional
    public void addCourse(Long teacherId, Long courseId) {
        logger.info("Adding course for teacher ID: {}", teacherId);
        if (teacherId == null || courseId == null) {
            logger.warn("Attempted to add course with null teacherId or courseId");
            throw new IllegalArgumentException("Invalid teacher ID or course ID");
        }
        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + teacherId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        course.getTeachers().add(teacher);
        courseRepository.save(course);

        teacher.getCourses().add(course);
        userRepository.save(teacher);

        logger.info("Successfully added course with ID: {} to teacher with ID: {}", courseId, teacherId);
    }

    @Override
    public List<Course> getTeacherCourses(Long teacherId) {
        logger.info("Fetching courses for teacher ID: {}", teacherId);

        if (teacherId == null) {
            logger.warn("Attempted to get courses with null teacherId");
            throw new IllegalArgumentException("Invalid teacher ID");
        }

        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + teacherId));

        List<Course> courses = teacher.getCourses();
        logger.info("Found {} courses for teacher ID: {}", courses.size(), teacherId);

        return courses;    }

    @Override
    public List<Lesson> getUpcomingLessons(Long teacherId) {
        logger.info("Fetching upcoming lessons for teacher ID: {}", teacherId);

        if (teacherId == null) {
            logger.warn("Attempted to get lessons with null teacherId");
            throw new IllegalArgumentException("Invalid teacher ID");
        }

        // Get teacher's courses
        List<Course> teacherCourses = getTeacherCourses(teacherId);

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
}

