package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import io.jos.onlinelearningplatform.service.AdminService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

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
        return userRepository.countByUserType(Student.class);
    }

    @Override
    public long getTotalTeachers() {
        return userRepository.countByUserType(Teacher.class);
    }

    @Override
    public long getTotalCourses() {
        return courseRepository.count();
    }

    @Override
    public long getTotalLessons() {
        return lessonRepository.count();
    }

    @Override
    public List<User> getRecentUsers(int limit) {
        return userRepository.findTopByOrderByIdDesc(PageRequest.of(0, limit));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    @Override
    @Transactional
    public void createCourse(String title, String description) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    @Override
    @Transactional
    public void toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}