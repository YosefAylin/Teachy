package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.Course;
import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;
import io.jos.onlinelearningplatform.model.User;
import io.jos.onlinelearningplatform.repository.CourseRepository;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            CourseRepository courseRepository,
                            LessonRepository lessonRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public long getTotalStudents() {
        logger.debug("Getting total student count");
        long count = userRepository.countByUserType(Student.class);
        logger.info("Total students: {}", count);
        return count;
    }

    @Override
    public long getTotalTeachers() {
        logger.debug("Getting total teacher count");
        long count = userRepository.countByUserType(Teacher.class);
        logger.info("Total teachers: {}", count);
        return count;
    }

    @Override
    public long getTotalCourses() {
        logger.debug("Getting total course count");
        long count = courseRepository.count();
        logger.info("Total courses: {}", count);
        return count;
    }

    @Override
    public long getTotalLessons() {
        logger.debug("Getting total lesson count");
        long count = lessonRepository.count();
        logger.info("Total lessons: {}", count);
        return count;
    }

    @Override
    public List<User> getRecentUsers(int limit) {
        logger.debug("Getting recent users with limit: {}", limit);
        List<User> users = userRepository.findTopByOrderByIdDesc(PageRequest.of(0, limit));
        logger.info("Retrieved {} recent users", users.size());
        return users;
    }

    @Override
    public List<User> getAllUsers() {
        logger.debug("Getting all users");
        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} total users", users.size());
        return users;
    }

    @Override
    public List<Course> getAllCourses() {
        logger.debug("Getting all courses");
        List<Course> courses = courseRepository.findAll();
        logger.info("Retrieved {} courses", courses.size());
        return courses;
    }

    @Override
    public List<Lesson> getAllLessons() {
        logger.debug("Getting all lessons");
        List<Lesson> lessons = lessonRepository.findAll();
        logger.info("Retrieved {} lessons", lessons.size());
        return lessons;
    }

    @Override
    @Transactional
    public void createCourse(String title, String description) {
        logger.debug("Creating new course with title: {}", title);
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        courseRepository.save(course);
        logger.info("Successfully created course: {}", title);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        logger.debug("Deleting course with ID: {}", courseId);
        courseRepository.deleteById(courseId);
        logger.info("Successfully deleted course with ID: {}", courseId);
    }

    @Override
    @Transactional
    public void toggleUserActive(Long userId) {
        logger.debug("Toggling active status for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean previousStatus = user.isActive();
        user.setActive(!user.isActive());
        userRepository.save(user);
        logger.info("Toggled user {} active status from {} to {}", userId, previousStatus, user.isActive());
    }
}