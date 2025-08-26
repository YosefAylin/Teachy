package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.cache.GlobalCacheService;
import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import io.jos.onlinelearningplatform.service.impl.TeacherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(TeacherServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private TeacherCourseRepository teacherCourseRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private GlobalCacheService globalCacheService;

    private TeacherService teacherService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        teacherService = new TeacherServiceImpl(userRepository, courseRepository, lessonRepository,
                teacherCourseRepository, scheduleRepository, globalCacheService);
        logger.info("TeacherServiceTest setup completed");
    }

    @Test
    @DisplayName("Get Students - Success")
    void getStudents_Success() {
        logger.info("Testing getStudents");
        // Arrange
        Long teacherId = 1L;
        Student student1 = new Student();
        student1.setUsername("student1");
        Student student2 = new Student();
        student2.setUsername("student2");

        Lesson lesson1 = new Lesson();
        lesson1.setStudent(student1);
        Lesson lesson2 = new Lesson();
        lesson2.setStudent(student2);
        Lesson lesson3 = new Lesson();
        lesson3.setStudent(student1); // Duplicate student

        when(lessonRepository.findByTeacherId(teacherId))
            .thenReturn(Arrays.asList(lesson1, lesson2, lesson3));

        // Act
        List<Student> result = teacherService.getStudents(teacherId);

        // Assert
        assertEquals(2, result.size()); // Should be distinct students
        verify(lessonRepository).findByTeacherId(teacherId);
        logger.info("getStudents test passed");
    }

    @Test
    @DisplayName("Get Students - Null Teacher ID")
    void getStudents_NullTeacherId_ThrowsException() {
        logger.info("Testing getStudents with null teacher ID");
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> teacherService.getStudents(null));
        assertEquals("Invalid teacher ID", exception.getMessage());
        logger.info("getStudents null teacher ID test passed");
    }

    @Test
    @DisplayName("Get Teacher ID By Username - Success")
    void getTeacherIdByUsername_Success() {
        logger.info("Testing getTeacherIdByUsername");
        // Arrange
        String username = "teacher1";
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(teacher));

        // Act
        Long result = teacherService.getTeacherIdByUsername(username);

        // Assert
        assertEquals(1L, result);
        verify(userRepository).findByUsername(username);
        logger.info("getTeacherIdByUsername test passed");
    }

    @Test
    @DisplayName("Get Teacher ID By Username - Invalid Username")
    void getTeacherIdByUsername_InvalidUsername_ThrowsException() {
        logger.info("Testing getTeacherIdByUsername with invalid username");
        // Act & Assert
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
            () -> teacherService.getTeacherIdByUsername(""));
        assertEquals("Invalid username", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
            () -> teacherService.getTeacherIdByUsername(null));
        assertEquals("Invalid username", exception2.getMessage());

        logger.info("getTeacherIdByUsername invalid username test passed");
    }

    @Test
    @DisplayName("Get Teacher ID By Username - User Not Found")
    void getTeacherIdByUsername_UserNotFound_ThrowsException() {
        logger.info("Testing getTeacherIdByUsername with non-existent user");
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> teacherService.getTeacherIdByUsername(username));
        assertTrue(exception.getMessage().contains("User not found"));
        logger.info("getTeacherIdByUsername user not found test passed");
    }

    @Test
    @DisplayName("Get Available Courses - Success")
    void getAvailableCourses_Success() {
        logger.info("Testing getAvailableCourses");
        // Arrange
        Course course1 = new Course();
        course1.setTitle("Java Programming");
        Course course2 = new Course();
        course2.setTitle("Spring Boot");
        List<Course> expectedCourses = Arrays.asList(course1, course2);

        when(courseRepository.findAll()).thenReturn(expectedCourses);

        // Act
        List<Course> result = teacherService.getAvailableCourses();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Java Programming", result.get(0).getTitle());
        assertEquals("Spring Boot", result.get(1).getTitle());
        verify(courseRepository).findAll();
        logger.info("getAvailableCourses test passed");
    }

    @Test
    @DisplayName("Add Teachable Course - Success")
    void addTeachableCourse_Success() {
        logger.info("Testing addTeachableCourse");
        // Arrange
        Long teacherId = 1L;
        Long courseId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        Course course = new Course();
        course.setId(courseId);

        when(teacherCourseRepository.existsByTeacherIdAndCourseId(teacherId, courseId)).thenReturn(false);
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(teacherCourseRepository.save(any(TeacherCourse.class))).thenReturn(new TeacherCourse());

        // Act
        assertDoesNotThrow(() -> teacherService.addTeachableCourse(teacherId, courseId));

        // Assert
        verify(teacherCourseRepository).existsByTeacherIdAndCourseId(teacherId, courseId);
        verify(userRepository).findById(teacherId);
        verify(courseRepository).findById(courseId);
        verify(teacherCourseRepository).save(any(TeacherCourse.class));
        logger.info("addTeachableCourse test passed");
    }

    @Test
    @DisplayName("Add Teachable Course - Already Exists")
    void addTeachableCourse_AlreadyExists_DoesNothing() {
        logger.info("Testing addTeachableCourse when course already exists");
        // Arrange
        Long teacherId = 1L;
        Long courseId = 1L;
        when(teacherCourseRepository.existsByTeacherIdAndCourseId(teacherId, courseId)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> teacherService.addTeachableCourse(teacherId, courseId));

        // Assert
        verify(teacherCourseRepository).existsByTeacherIdAndCourseId(teacherId, courseId);
        verify(teacherCourseRepository, never()).save(any(TeacherCourse.class));
        logger.info("addTeachableCourse already exists test passed");
    }

    @Test
    @DisplayName("Remove Teachable Course - Success")
    void removeTeachableCourse_Success() {
        logger.info("Testing removeTeachableCourse");
        // Arrange
        Long teacherId = 1L;
        Long courseId = 1L;

        // Act
        assertDoesNotThrow(() -> teacherService.removeTeachableCourse(teacherId, courseId));

        // Assert
        verify(teacherCourseRepository).deleteByTeacherIdAndCourseId(teacherId, courseId);
        logger.info("removeTeachableCourse test passed");
    }

    @Test
    @DisplayName("Get Teachable Courses - Success")
    void getTeachableCourses_Success() {
        logger.info("Testing getTeachableCourses");
        // Arrange
        Long teacherId = 1L;
        Course course1 = new Course();
        course1.setTitle("Java");
        Course course2 = new Course();
        course2.setTitle("Spring");

        TeacherCourse tc1 = new TeacherCourse();
        tc1.setCourse(course1);
        TeacherCourse tc2 = new TeacherCourse();
        tc2.setCourse(course2);

        when(teacherCourseRepository.findByTeacherId(teacherId))
            .thenReturn(Arrays.asList(tc1, tc2));

        // Act
        List<Course> result = teacherService.getTeachableCourses(teacherId);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getTitle());
        assertEquals("Spring", result.get(1).getTitle());
        verify(teacherCourseRepository).findByTeacherId(teacherId);
        logger.info("getTeachableCourses test passed");
    }

    @Test
    @DisplayName("Count Pending Lessons - Success")
    void countPendingLessons_Success() {
        logger.info("Testing countPendingLessons");
        // Arrange
        Long teacherId = 1L;
        when(lessonRepository.countByTeacherIdAndStatus(teacherId, "PENDING")).thenReturn(3);

        // Act
        int result = teacherService.countPendingLessons(teacherId);

        // Assert
        assertEquals(3, result);
        verify(lessonRepository).countByTeacherIdAndStatus(teacherId, "PENDING");
        logger.info("countPendingLessons test passed");
    }

    @Test
    @DisplayName("Get Teacher Profile - Success")
    void getTeacherProfile_Success() {
        logger.info("Testing getTeacherProfile");
        // Arrange
        Long teacherId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setUsername("teacher1");

        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        // Act
        Teacher result = teacherService.getTeacherProfile(teacherId);

        // Assert
        assertNotNull(result);
        assertEquals(teacherId, result.getId());
        assertEquals("teacher1", result.getUsername());
        verify(userRepository).findById(teacherId);
        logger.info("getTeacherProfile test passed");
    }

    @Test
    @DisplayName("Get Next Lesson - Success")
    void getNextLesson_Success() {
        logger.info("Testing getNextLesson");
        // Arrange
        Long teacherId = 1L;
        Lesson nextLesson = new Lesson();
        nextLesson.setTimestamp(LocalDateTime.now().plusHours(1));

        when(lessonRepository.findUpcomingByTeacher(eq(teacherId), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(nextLesson));

        // Act
        Lesson result = teacherService.getNextLesson(teacherId);

        // Assert
        assertNotNull(result);
        assertEquals(nextLesson, result);
        verify(lessonRepository).findUpcomingByTeacher(eq(teacherId), any(LocalDateTime.class));
        logger.info("getNextLesson test passed");
    }

    @Test
    @DisplayName("Accept Lesson - Success")
    void acceptLesson_Success() {
        logger.info("Testing acceptLesson");
        // Arrange
        Long lessonId = 1L;
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setStatus("PENDING");

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(lesson)).thenReturn(lesson);

        // Act
        assertDoesNotThrow(() -> teacherService.acceptLesson(lessonId));

        // Assert
        assertEquals("ACCEPTED", lesson.getStatus());
        verify(lessonRepository).findById(lessonId);
        verify(lessonRepository).save(lesson);
        logger.info("acceptLesson test passed");
    }
}
