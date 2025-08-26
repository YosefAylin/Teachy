package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.cache.GlobalCacheService;
import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.ScheduleRepository;
import io.jos.onlinelearningplatform.service.impl.StudentServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceTest.class);

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private GlobalCacheService globalCacheService;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        studentService = new StudentServiceImpl(scheduleRepository, lessonRepository, globalCacheService);
        logger.info("StudentServiceTest setup completed");
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

        Student student = new Student();
        student.setId(studentId);
        Teacher teacher = new Teacher();
        teacher.setId(2L);

        Lesson lesson1 = new Lesson();
        lesson1.setStudent(student);
        lesson1.setTeacher(teacher);
        lesson1.setTimestamp(LocalDateTime.of(2025, 8, 15, 10, 0));
        lesson1.setStatus("ACCEPTED");

        when(lessonRepository.findByStudentIdAndTimestampBetweenOrderByTimestampAsc(studentId, start, end))
            .thenReturn(Arrays.asList(lesson1));

        // Act
        List<Schedule> result = studentService.getSchedulesForMonth(studentId, start, end);

        // Assert
        assertEquals(1, result.size());
        assertEquals(lesson1.getTimestamp(), result.get(0).getScheduledTime());
        assertEquals(lesson1.getStatus(), result.get(0).getStatus());
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
        nextLesson.setTimestamp(LocalDateTime.now().plusHours(2));

        when(lessonRepository.findUpcomingByStudent(eq(studentId), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(nextLesson));

        // Act
        Lesson result = studentService.getNextLesson(studentId);

        // Assert
        assertNotNull(result);
        assertEquals(nextLesson, result);
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
}
