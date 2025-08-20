package io.jos.onlinelearningplatform.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private PasswordEncoder passwordEncoder;

    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        teacherService = new TeacherServiceImpl(userRepository, courseRepository, lessonRepository,
                passwordEncoder, teacherCourseRepository, scheduleRepository);
        logger.info("TeacherServiceTest setup completed");
    }

    @Test
    @DisplayName("Handle Booking - Valid Lesson ID")
    void handleBooking_ValidLessonId_Success() {
        logger.info("Testing handleBooking with valid lesson ID");
        // Arrange
        Long lessonId = 1L;

        // Act & Assert
        assertDoesNotThrow(() -> teacherService.handleBooking(lessonId));
        logger.info("handleBooking valid lesson ID test passed");
    }

    @Test
    @DisplayName("Handle Booking - Null Lesson ID")
    void handleBooking_NullLessonId_ThrowsException() {
        logger.info("Testing handleBooking with null lesson ID");
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> teacherService.handleBooking(null));
        assertEquals("Invalid lesson ID", exception.getMessage());
        logger.info("handleBooking null lesson ID test passed");
    }

    @Test
    @DisplayName("Get Upcoming Lessons - Success")
    void getUpcomingLessons_Success() {
        logger.info("Testing getUpcomingLessons");
        // Arrange
        Long teacherId = 1L;
        Course course1 = new Course();
        course1.setId(1L);
        Course course2 = new Course();
        course2.setId(2L);
        List<Course> teacherCourses = Arrays.asList(course1, course2);

        Lesson lesson1 = new Lesson();
        lesson1.setTimestamp(LocalDateTime.now().plusHours(1));
        Lesson lesson2 = new Lesson();
        lesson2.setTimestamp(LocalDateTime.now().plusHours(2));
        List<Lesson> upcomingLessons = Arrays.asList(lesson1, lesson2);

        when(teacherCourseRepository.findByTeacherId(teacherId))
            .thenReturn(Arrays.asList(createTeacherCourse(course1), createTeacherCourse(course2)));
        when(lessonRepository.findUpcomingLessonsByCourseIds(any(List.class), any(LocalDateTime.class)))
            .thenReturn(upcomingLessons);

        // Act
        List<Lesson> result = teacherService.getUpcomingLessons(teacherId);

        // Assert
        assertEquals(2, result.size());
        verify(teacherCourseRepository).findByTeacherId(teacherId);
        verify(lessonRepository).findUpcomingLessonsByCourseIds(any(List.class), any(LocalDateTime.class));
        logger.info("getUpcomingLessons test passed");
    }

    @Test
    @DisplayName("Get Upcoming Lessons - Null Teacher ID")
    void getUpcomingLessons_NullTeacherId_ThrowsException() {
        logger.info("Testing getUpcomingLessons with null teacher ID");
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> teacherService.getUpcomingLessons(null));
        assertEquals("Invalid teacher ID", exception.getMessage());
        logger.info("getUpcomingLessons null teacher ID test passed");
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
    @DisplayName("Get Teacher ID By Username - Teacher Not Found")
    void getTeacherIdByUsername_TeacherNotFound_ThrowsException() {
        logger.info("Testing getTeacherIdByUsername with non-existent teacher");
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> teacherService.getTeacherIdByUsername(username));
        assertTrue(exception.getMessage().contains("Teacher not found"));
        logger.info("getTeacherIdByUsername teacher not found test passed");
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
        course2.setTitle("Python");

        when(teacherCourseRepository.findByTeacherId(teacherId))
            .thenReturn(Arrays.asList(createTeacherCourse(course1), createTeacherCourse(course2)));

        // Act
        List<Course> result = teacherService.getTeachableCourses(teacherId);

        // Assert
        assertEquals(2, result.size());
        verify(teacherCourseRepository).findByTeacherId(teacherId);
        logger.info("getTeachableCourses test passed");
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
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        // Act
        assertDoesNotThrow(() -> teacherService.acceptLesson(lessonId));

        // Assert
        assertEquals("ACCEPTED", lesson.getStatus());
        verify(lessonRepository).findById(lessonId);
        verify(lessonRepository).save(lesson);
        logger.info("acceptLesson test passed");
    }

    @Test
    @DisplayName("Accept Lesson - Lesson Not Found")
    void acceptLesson_LessonNotFound_ThrowsException() {
        logger.info("Testing acceptLesson with non-existent lesson");
        // Arrange
        Long lessonId = 999L;
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> teacherService.acceptLesson(lessonId));
        assertTrue(exception.getMessage().contains("Lesson not found"));
        logger.info("acceptLesson lesson not found test passed");
    }

    private TeacherCourse createTeacherCourse(Course course) {
        TeacherCourse tc = new TeacherCourse();
        tc.setCourse(course);
        return tc;
    }
}
