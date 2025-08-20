package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.CourseRepository;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.impl.LessonServiceImpl;
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

class LessonServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(LessonServiceTest.class);

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    private LessonService lessonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lessonService = new LessonServiceImpl(lessonRepository, userRepository, courseRepository);
        logger.info("LessonServiceTest setup completed");
    }

    @Test
    @DisplayName("Request Lesson - Success")
    void requestLesson_ValidData_Success() {
        logger.info("Testing requestLesson with valid data");
        // Arrange
        Long studentId = 1L;
        Long teacherId = 2L;
        Long courseId = 3L;
        LocalDateTime futureTimestamp = LocalDateTime.now().plusDays(1);

        Student student = new Student();
        student.setId(studentId);
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        Course course = new Course();
        course.setId(courseId);

        Lesson savedLesson = new Lesson();
        savedLesson.setId(1L);
        savedLesson.setStatus("PENDING");

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(savedLesson);

        // Act
        Lesson result = lessonService.requestLesson(studentId, teacherId, courseId, futureTimestamp);

        // Assert
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(userRepository).findById(studentId);
        verify(userRepository).findById(teacherId);
        verify(courseRepository).findById(courseId);
        verify(lessonRepository).save(any(Lesson.class));
        logger.info("requestLesson success test passed");
    }

    @Test
    @DisplayName("Request Lesson - Student Not Found")
    void requestLesson_StudentNotFound_ThrowsException() {
        logger.info("Testing requestLesson with non-existent student");
        // Arrange
        Long studentId = 999L;
        Long teacherId = 2L;
        Long courseId = 3L;
        LocalDateTime futureTimestamp = LocalDateTime.now().plusDays(1);

        when(userRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> lessonService.requestLesson(studentId, teacherId, courseId, futureTimestamp));

        assertTrue(exception.getMessage().contains("Student not found"));
        verify(userRepository).findById(studentId);
        verify(lessonRepository, never()).save(any(Lesson.class));
        logger.info("requestLesson student not found test passed");
    }

    @Test
    @DisplayName("Request Lesson - Teacher Not Found")
    void requestLesson_TeacherNotFound_ThrowsException() {
        logger.info("Testing requestLesson with non-existent teacher");
        // Arrange
        Long studentId = 1L;
        Long teacherId = 999L;
        Long courseId = 3L;
        LocalDateTime futureTimestamp = LocalDateTime.now().plusDays(1);

        Student student = new Student();
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(userRepository.findById(teacherId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> lessonService.requestLesson(studentId, teacherId, courseId, futureTimestamp));

        assertTrue(exception.getMessage().contains("Teacher not found"));
        verify(userRepository).findById(studentId);
        verify(userRepository).findById(teacherId);
        verify(lessonRepository, never()).save(any(Lesson.class));
        logger.info("requestLesson teacher not found test passed");
    }

    @Test
    @DisplayName("Request Lesson - Course Not Found")
    void requestLesson_CourseNotFound_ThrowsException() {
        logger.info("Testing requestLesson with non-existent course");
        // Arrange
        Long studentId = 1L;
        Long teacherId = 2L;
        Long courseId = 999L;
        LocalDateTime futureTimestamp = LocalDateTime.now().plusDays(1);

        Student student = new Student();
        Teacher teacher = new Teacher();
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> lessonService.requestLesson(studentId, teacherId, courseId, futureTimestamp));

        assertTrue(exception.getMessage().contains("Course not found"));
        verify(userRepository).findById(studentId);
        verify(userRepository).findById(teacherId);
        verify(courseRepository).findById(courseId);
        verify(lessonRepository, never()).save(any(Lesson.class));
        logger.info("requestLesson course not found test passed");
    }

    @Test
    @DisplayName("Request Lesson - Past Date")
    void requestLesson_PastDate_ThrowsException() {
        logger.info("Testing requestLesson with past date");
        // Arrange
        Long studentId = 1L;
        Long teacherId = 2L;
        Long courseId = 3L;
        LocalDateTime pastTimestamp = LocalDateTime.now().minusDays(1);

        Student student = new Student();
        Teacher teacher = new Teacher();
        Course course = new Course();
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> lessonService.requestLesson(studentId, teacherId, courseId, pastTimestamp));

        assertTrue(exception.getMessage().contains("Scheduled date must be today or in the future"));
        verify(lessonRepository, never()).save(any(Lesson.class));
        logger.info("requestLesson past date test passed");
    }

    @Test
    @DisplayName("Get Upcoming For Student - Success")
    void getUpcomingForStudent_Success() {
        logger.info("Testing getUpcomingForStudent");
        // Arrange
        Long studentId = 1L;
        Lesson lesson1 = new Lesson();
        lesson1.setStatus("PENDING");
        Lesson lesson2 = new Lesson();
        lesson2.setStatus("ACCEPTED");
        List<Lesson> expectedLessons = Arrays.asList(lesson1, lesson2);

        when(lessonRepository.findByStudent_IdAndStatusInAndTimestampGreaterThanEqualOrderByTimestampAsc(
            eq(studentId), any(List.class), any(LocalDateTime.class))).thenReturn(expectedLessons);

        // Act
        List<Lesson> result = lessonService.getUpcomingForStudent(studentId);

        // Assert
        assertEquals(2, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
        assertEquals("ACCEPTED", result.get(1).getStatus());
        verify(lessonRepository).findByStudent_IdAndStatusInAndTimestampGreaterThanEqualOrderByTimestampAsc(
            eq(studentId), any(List.class), any(LocalDateTime.class));
        logger.info("getUpcomingForStudent test passed");
    }

    @Test
    @DisplayName("Get Past For Student - Success")
    void getPastForStudent_Success() {
        logger.info("Testing getPastForStudent");
        // Arrange
        Long studentId = 1L;
        Lesson acceptedPastLesson = new Lesson();
        acceptedPastLesson.setStatus("ACCEPTED");
        acceptedPastLesson.setTimestamp(LocalDateTime.now().minusDays(1));

        Lesson rejectedLesson = new Lesson();
        rejectedLesson.setStatus("REJECTED");
        rejectedLesson.setTimestamp(LocalDateTime.now().minusDays(2));

        when(lessonRepository.findByStudent_IdAndStatusInAndTimestampLessThanOrderByTimestampDesc(
            eq(studentId), any(List.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(acceptedPastLesson));

        when(lessonRepository.findByStudent_IdAndStatusOrderByTimestampDesc(studentId, "REJECTED"))
            .thenReturn(Arrays.asList(rejectedLesson));

        // Act
        List<Lesson> result = lessonService.getPastForStudent(studentId);

        // Assert
        assertEquals(2, result.size());
        verify(lessonRepository).findByStudent_IdAndStatusInAndTimestampLessThanOrderByTimestampDesc(
            eq(studentId), any(List.class), any(LocalDateTime.class));
        verify(lessonRepository).findByStudent_IdAndStatusOrderByTimestampDesc(studentId, "REJECTED");
        logger.info("getPastForStudent test passed");
    }

    @Test
    @DisplayName("Get Upcoming For Student - Empty Result")
    void getUpcomingForStudent_EmptyResult() {
        logger.info("Testing getUpcomingForStudent with no results");
        // Arrange
        Long studentId = 1L;
        when(lessonRepository.findByStudent_IdAndStatusInAndTimestampGreaterThanEqualOrderByTimestampAsc(
            eq(studentId), any(List.class), any(LocalDateTime.class))).thenReturn(Arrays.asList());

        // Act
        List<Lesson> result = lessonService.getUpcomingForStudent(studentId);

        // Assert
        assertTrue(result.isEmpty());
        verify(lessonRepository).findByStudent_IdAndStatusInAndTimestampGreaterThanEqualOrderByTimestampAsc(
            eq(studentId), any(List.class), any(LocalDateTime.class));
        logger.info("getUpcomingForStudent empty result test passed");
    }

    @Test
    @DisplayName("Get Past For Student - Empty Result")
    void getPastForStudent_EmptyResult() {
        logger.info("Testing getPastForStudent with no results");
        // Arrange
        Long studentId = 1L;
        when(lessonRepository.findByStudent_IdAndStatusInAndTimestampLessThanOrderByTimestampDesc(
            eq(studentId), any(List.class), any(LocalDateTime.class))).thenReturn(Arrays.asList());
        when(lessonRepository.findByStudent_IdAndStatusOrderByTimestampDesc(studentId, "REJECTED"))
            .thenReturn(Arrays.asList());

        // Act
        List<Lesson> result = lessonService.getPastForStudent(studentId);

        // Assert
        assertTrue(result.isEmpty());
        verify(lessonRepository).findByStudent_IdAndStatusInAndTimestampLessThanOrderByTimestampDesc(
            eq(studentId), any(List.class), any(LocalDateTime.class));
        verify(lessonRepository).findByStudent_IdAndStatusOrderByTimestampDesc(studentId, "REJECTED");
        logger.info("getPastForStudent empty result test passed");
    }
}
