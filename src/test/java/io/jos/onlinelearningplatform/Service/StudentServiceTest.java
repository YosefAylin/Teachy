package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.ScheduleRepository;
import io.jos.onlinelearningplatform.repository.UserRepository;
import io.jos.onlinelearningplatform.service.impl.StudentServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private LessonRepository lessonRepository;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        studentService = new StudentServiceImpl(userRepository, passwordEncoder, scheduleRepository, lessonRepository);
        logger.info("StudentServiceTest setup completed");
    }

    @Test
    @DisplayName("Find Teacher By Subject - Valid Subject")
    void findTeacherBySubject_ValidSubject_Success() {
        logger.info("Testing findTeacherBySubject with valid subject");
        // Arrange
        String subject = "Mathematics";

        // Act & Assert
        assertDoesNotThrow(() -> studentService.findTeacherBySubject(subject));
        logger.info("findTeacherBySubject valid subject test passed");
    }

    @Test
    @DisplayName("Find Teacher By Subject - Empty Subject")
    void findTeacherBySubject_EmptySubject_DoesNothing() {
        logger.info("Testing findTeacherBySubject with empty subject");
        // Arrange
        String subject = "";

        // Act & Assert
        assertDoesNotThrow(() -> studentService.findTeacherBySubject(subject));
        logger.info("findTeacherBySubject empty subject test passed");
    }

    @Test
    @DisplayName("Find Teacher By Subject - Null Subject")
    void findTeacherBySubject_NullSubject_DoesNothing() {
        logger.info("Testing findTeacherBySubject with null subject");
        // Act & Assert
        assertDoesNotThrow(() -> studentService.findTeacherBySubject(null));
        logger.info("findTeacherBySubject null subject test passed");
    }

    @Test
    @DisplayName("Book Teacher - Valid Data")
    void bookTeacher_ValidData_Success() {
        logger.info("Testing bookTeacher with valid data");
        // Arrange
        String teacherId = "123";
        String dateTime = "2025-12-01T10:00";

        // Act & Assert
        assertDoesNotThrow(() -> studentService.bookTeacher(teacherId, dateTime));
        logger.info("bookTeacher valid data test passed");
    }

    @Test
    @DisplayName("Book Teacher - Invalid Data")
    void bookTeacher_InvalidData_ThrowsException() {
        logger.info("Testing bookTeacher with invalid data");
        // Act & Assert
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
            () -> studentService.bookTeacher("", "2025-12-01T10:00"));
        assertEquals("Invalid booking data", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
            () -> studentService.bookTeacher("123", ""));
        assertEquals("Invalid booking data", exception2.getMessage());

        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class,
            () -> studentService.bookTeacher(null, "2025-12-01T10:00"));
        assertEquals("Invalid booking data", exception3.getMessage());

        logger.info("bookTeacher invalid data test passed");
    }

    @Test
    @DisplayName("Cancel Booking - Valid Lesson ID")
    void cancelBooking_ValidLessonId_Success() {
        logger.info("Testing cancelBooking with valid lesson ID");
        // Arrange
        String lessonId = "123";

        // Act & Assert
        assertDoesNotThrow(() -> studentService.cancelBooking(lessonId));
        logger.info("cancelBooking valid lesson ID test passed");
    }

    @Test
    @DisplayName("Cancel Booking - Invalid Lesson ID")
    void cancelBooking_InvalidLessonId_ThrowsException() {
        logger.info("Testing cancelBooking with invalid lesson ID");
        // Act & Assert
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
            () -> studentService.cancelBooking(""));
        assertEquals("Invalid lesson ID", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
            () -> studentService.cancelBooking(null));
        assertEquals("Invalid lesson ID", exception2.getMessage());

        logger.info("cancelBooking invalid lesson ID test passed");
    }

    @Test
    @DisplayName("Make Payment - Valid Data")
    void makePayment_ValidData_Success() {
        logger.info("Testing makePayment with valid data");
        // Arrange
        String lessonId = "123";
        String paymentMethod = "Credit Card";

        // Act & Assert
        assertDoesNotThrow(() -> studentService.makePayment(lessonId, paymentMethod));
        logger.info("makePayment valid data test passed");
    }

    @Test
    @DisplayName("Get Student Schedule - Success")
    void getStudentSchedule_Success() {
        logger.info("Testing getStudentSchedule");
        // Arrange
        Long studentId = 1L;
        Schedule schedule1 = new Schedule();
        Schedule schedule2 = new Schedule();
        List<Schedule> expectedSchedules = Arrays.asList(schedule1, schedule2);

        when(scheduleRepository.findByStudentIdOrderByScheduledTimeAsc(studentId)).thenReturn(expectedSchedules);

        // Act
        List<Schedule> result = studentService.getStudentSchedule(studentId);

        // Assert
        assertEquals(2, result.size());
        verify(scheduleRepository).findByStudentIdOrderByScheduledTimeAsc(studentId);
        logger.info("getStudentSchedule test passed");
    }

    @Test
    @DisplayName("Get Upcoming Schedule - Success")
    void getUpcomingSchedule_Success() {
        logger.info("Testing getUpcomingSchedule");
        // Arrange
        Long studentId = 1L;
        Schedule schedule1 = new Schedule();
        schedule1.setScheduledTime(LocalDateTime.now().plusHours(1));
        List<Schedule> expectedSchedules = Arrays.asList(schedule1);

        when(scheduleRepository.findByStudentIdAndScheduledTimeAfterOrderByScheduledTimeAsc(
            eq(studentId), any(LocalDateTime.class))).thenReturn(expectedSchedules);

        // Act
        List<Schedule> result = studentService.getUpcomingSchedule(studentId);

        // Assert
        assertEquals(1, result.size());
        verify(scheduleRepository).findByStudentIdAndScheduledTimeAfterOrderByScheduledTimeAsc(
            eq(studentId), any(LocalDateTime.class));
        logger.info("getUpcomingSchedule test passed");
    }

    @Test
    @DisplayName("Get Schedules For Month - Success")
    void getSchedulesForMonth_Success() {
        logger.info("Testing getSchedulesForMonth");
        // Arrange
        Long studentId = 1L;
        LocalDateTime start = LocalDateTime.of(2025, 8, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 8, 31, 23, 59);

        Lesson lesson1 = new Lesson();
        lesson1.setTimestamp(LocalDateTime.of(2025, 8, 15, 10, 0));
        lesson1.setStatus("ACCEPTED");
        lesson1.setStudent(new Student());
        lesson1.setTeacher(new Teacher());

        when(lessonRepository.findByStudentIdAndTimestampBetweenOrderByTimestampAsc(studentId, start, end))
            .thenReturn(Arrays.asList(lesson1));

        // Act
        List<Schedule> result = studentService.getSchedulesForMonth(studentId, start, end);

        // Assert
        assertEquals(1, result.size());
        assertEquals("ACCEPTED", result.get(0).getStatus());
        verify(lessonRepository).findByStudentIdAndTimestampBetweenOrderByTimestampAsc(studentId, start, end);
        logger.info("getSchedulesForMonth test passed");
    }

    @Test
    @DisplayName("Get Next Lesson - Success")
    void getNextLesson_Success() {
        logger.info("Testing getNextLesson");
        // Arrange
        Long studentId = 1L;
        Lesson nextLesson = new Lesson();
        nextLesson.setId(1L);
        nextLesson.setTimestamp(LocalDateTime.now().plusHours(2));

        when(lessonRepository.findUpcomingByStudent(eq(studentId), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(nextLesson));

        // Act
        Lesson result = studentService.getNextLesson(studentId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(lessonRepository).findUpcomingByStudent(eq(studentId), any(LocalDateTime.class));
        logger.info("getNextLesson test passed");
    }

    @Test
    @DisplayName("Get Next Lesson - No Upcoming Lessons")
    void getNextLesson_NoUpcomingLessons_ReturnsNull() {
        logger.info("Testing getNextLesson with no upcoming lessons");
        // Arrange
        Long studentId = 1L;
        when(lessonRepository.findUpcomingByStudent(eq(studentId), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList());

        // Act
        Lesson result = studentService.getNextLesson(studentId);

        // Assert
        assertNull(result);
        verify(lessonRepository).findUpcomingByStudent(eq(studentId), any(LocalDateTime.class));
        logger.info("getNextLesson no upcoming lessons test passed");
    }

    @Test
    @DisplayName("Get Student Schedule - Empty Result")
    void getStudentSchedule_EmptyResult() {
        logger.info("Testing getStudentSchedule with empty result");
        // Arrange
        Long studentId = 1L;
        when(scheduleRepository.findByStudentIdOrderByScheduledTimeAsc(studentId)).thenReturn(Arrays.asList());

        // Act
        List<Schedule> result = studentService.getStudentSchedule(studentId);

        // Assert
        assertTrue(result.isEmpty());
        verify(scheduleRepository).findByStudentIdOrderByScheduledTimeAsc(studentId);
        logger.info("getStudentSchedule empty result test passed");
    }
}
