package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.cache.GlobalCacheService;
import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Schedule;
import io.jos.onlinelearningplatform.repository.LessonRepository;
import io.jos.onlinelearningplatform.repository.ScheduleRepository;
import io.jos.onlinelearningplatform.service.StudentService;
import io.jos.onlinelearningplatform.util.ScheduleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final ScheduleRepository scheduleRepository;
    private final LessonRepository lessonRepository;
    private final GlobalCacheService globalCache;

    public StudentServiceImpl(ScheduleRepository scheduleRepository, LessonRepository lessonRepository, GlobalCacheService globalCache) {
        this.scheduleRepository = scheduleRepository;
        this.lessonRepository = lessonRepository;
        this.globalCache = globalCache;
    }

    @Override
    public List<Schedule> getStudentSchedule(Long studentId) {
        logger.debug("Getting student schedule for student ID: {}", studentId);
        // No caching for schedules - only users and lessons are cached
        List<Schedule> schedules = scheduleRepository.findByStudentIdOrderByScheduledTimeAsc(studentId);
        logger.info("Retrieved {} schedule entries for student ID: {}", schedules.size(), studentId);
        return schedules;
    }

    @Override
    public List<Schedule> getUpcomingSchedule(Long studentId) {
        logger.debug("Getting upcoming schedule for student ID: {}", studentId);
        List<Schedule> upcomingSchedules = scheduleRepository.findByStudentIdAndScheduledTimeAfterOrderByScheduledTimeAsc(
                studentId, LocalDateTime.now());
        logger.info("Found {} upcoming schedule entries for student ID: {}", upcomingSchedules.size(), studentId);
        return upcomingSchedules;
    }

    @Override
    public List<Schedule> getSchedulesForMonth(Long studentId, LocalDateTime start, LocalDateTime end) {
        logger.debug("Getting schedules for student ID: {} between {} and {}", studentId, start, end);
        List<Lesson> lessons = lessonRepository.findByStudentIdAndTimestampBetweenOrderByTimestampAsc(
                studentId, start, end);
        List<Schedule> schedules = ScheduleUtils.convertLessonsToSchedules(lessons);
        logger.info("Converted {} lessons to {} schedule entries for student ID: {} (period: {} to {})",
                   lessons.size(), schedules.size(), studentId, start, end);
        return schedules;
    }

    @Override
    public Lesson getNextLesson(Long studentId) {
        logger.debug("Getting next lesson for student ID: {}", studentId);
        String cacheKey = studentId + "_next";

        // Check global cache first for lesson
        Lesson cached = globalCache.getLesson(cacheKey, Lesson.class);
        if (cached != null) {
            logger.debug("Found next lesson in cache for student ID: {}", studentId);
            logger.info("Retrieved next lesson from cache for student ID: {} - lesson at {}",
                       studentId, cached.getTimestamp());
            return cached;
        }

        // If not in cache, fetch from database
        logger.debug("Next lesson not in cache, fetching from database for student ID: {}", studentId);
        List<Lesson> upcomingLessons = lessonRepository.findUpcomingByStudent(studentId, LocalDateTime.now());
        Lesson nextLesson = upcomingLessons.stream().findFirst().orElse(null);

        // Store in global cache
        if (nextLesson != null) {
            globalCache.putLesson(cacheKey, nextLesson);
            // Add null check for teacher
            String teacherInfo = nextLesson.getTeacher() != null ? nextLesson.getTeacher().getUsername() : "Unknown Teacher";
            logger.info("Found and cached next lesson for student ID: {} - lesson at {} with teacher: {}",
                       studentId, nextLesson.getTimestamp(), teacherInfo);
        } else {
            logger.info("No upcoming lessons found for student ID: {}", studentId);
        }

        return nextLesson;
    }
}
