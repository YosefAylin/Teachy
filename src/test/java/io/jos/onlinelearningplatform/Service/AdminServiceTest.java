package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.CourseRepository;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminServiceImpl(userRepository, courseRepository, lessonRepository);
        logger.info("AdminServiceTest setup completed");
    }

    @Test
    @DisplayName("Get Total Students - Success")
    void getTotalStudents_Success() {
        logger.info("Testing getTotalStudents");
        // Arrange
        when(userRepository.countByUserType(Student.class)).thenReturn(25);

        // Act
        long result = adminService.getTotalStudents();

        // Assert
        assertEquals(25, result);
        verify(userRepository).countByUserType(Student.class);
        logger.info("getTotalStudents test passed");
    }

    @Test
    @DisplayName("Get Total Teachers - Success")
    void getTotalTeachers_Success() {
        logger.info("Testing getTotalTeachers");
        // Arrange
        when(userRepository.countByUserType(Teacher.class)).thenReturn(10);

        // Act
        long result = adminService.getTotalTeachers();

        // Assert
        assertEquals(10, result);
        verify(userRepository).countByUserType(Teacher.class);
        logger.info("getTotalTeachers test passed");
    }

    @Test
    @DisplayName("Get Total Courses - Success")
    void getTotalCourses_Success() {
        logger.info("Testing getTotalCourses");
        // Arrange
        when(courseRepository.count()).thenReturn(15L);

        // Act
        long result = adminService.getTotalCourses();

        // Assert
        assertEquals(15L, result);
        verify(courseRepository).count();
        logger.info("getTotalCourses test passed");
    }

    @Test
    @DisplayName("Get Total Lessons - Success")
    void getTotalLessons_Success() {
        logger.info("Testing getTotalLessons");
        // Arrange
        when(lessonRepository.count()).thenReturn(100L);

        // Act
        long result = adminService.getTotalLessons();

        // Assert
        assertEquals(100L, result);
        verify(lessonRepository).count();
        logger.info("getTotalLessons test passed");
    }

    @Test
    @DisplayName("Get Recent Users - Success")
    void getRecentUsers_Success() {
        logger.info("Testing getRecentUsers");
        // Arrange
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findTopByOrderByIdDesc(any(PageRequest.class))).thenReturn(expectedUsers);

        // Act
        List<User> result = adminService.getRecentUsers(5);

        // Assert
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository).findTopByOrderByIdDesc(any(PageRequest.class));
        logger.info("getRecentUsers test passed");
    }

    @Test
    @DisplayName("Get All Users - Success")
    void getAllUsers_Success() {
        logger.info("Testing getAllUsers");
        // Arrange
        User user1 = new User();
        User user2 = new User();
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = adminService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userRepository).findAll();
        logger.info("getAllUsers test passed");
    }

    @Test
    @DisplayName("Get All Courses - Success")
    void getAllCourses_Success() {
        logger.info("Testing getAllCourses");
        // Arrange
        Course course1 = new Course();
        course1.setTitle("Java Programming");
        Course course2 = new Course();
        course2.setTitle("Spring Boot");
        List<Course> expectedCourses = Arrays.asList(course1, course2);

        when(courseRepository.findAll()).thenReturn(expectedCourses);

        // Act
        List<Course> result = adminService.getAllCourses();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Java Programming", result.get(0).getTitle());
        assertEquals("Spring Boot", result.get(1).getTitle());
        verify(courseRepository).findAll();
        logger.info("getAllCourses test passed");
    }

    @Test
    @DisplayName("Get All Lessons - Success")
    void getAllLessons_Success() {
        logger.info("Testing getAllLessons");
        // Arrange
        Lesson lesson1 = new Lesson();
        Lesson lesson2 = new Lesson();
        List<Lesson> expectedLessons = Arrays.asList(lesson1, lesson2);

        when(lessonRepository.findAll()).thenReturn(expectedLessons);

        // Act
        List<Lesson> result = adminService.getAllLessons();

        // Assert
        assertEquals(2, result.size());
        verify(lessonRepository).findAll();
        logger.info("getAllLessons test passed");
    }

    @Test
    @DisplayName("Create Course - Success")
    void createCourse_Success() {
        logger.info("Testing createCourse");
        // Arrange
        String title = "New Course";
        String description = "Course Description";
        Course savedCourse = new Course();
        savedCourse.setTitle(title);
        savedCourse.setDescription(description);

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // Act
        assertDoesNotThrow(() -> adminService.createCourse(title, description));

        // Assert
        verify(courseRepository).save(any(Course.class));
        logger.info("createCourse test passed");
    }

    @Test
    @DisplayName("Delete Course - Success")
    void deleteCourse_Success() {
        logger.info("Testing deleteCourse");
        // Arrange
        Long courseId = 1L;

        // Act
        assertDoesNotThrow(() -> adminService.deleteCourse(courseId));

        // Assert
        verify(courseRepository).deleteById(courseId);
        logger.info("deleteCourse test passed");
    }

    @Test
    @DisplayName("Toggle User Active - Success")
    void toggleUserActive_Success() {
        logger.info("Testing toggleUserActive");
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        assertDoesNotThrow(() -> adminService.toggleUserActive(userId));

        // Assert
        assertFalse(user.isActive()); // Should be toggled to false
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
        logger.info("toggleUserActive test passed");
    }

    @Test
    @DisplayName("Toggle User Active - User Not Found")
    void toggleUserActive_UserNotFound_ThrowsException() {
        logger.info("Testing toggleUserActive with non-existent user");
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> adminService.toggleUserActive(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        logger.info("toggleUserActive user not found test passed");
    }
}
