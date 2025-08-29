package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.cache.GlobalCacheService;
import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import io.jos.onlinelearningplatform.service.TeacherService;
import io.jos.onlinelearningplatform.util.ScheduleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final TeacherCourseRepository teacherCourseRepository;
    private final ScheduleRepository scheduleRepository;
    private final GlobalCacheService globalCache;

    public TeacherServiceImpl(UserRepository userRepository, CourseRepository courseRepository, LessonRepository lessonRepository, TeacherCourseRepository teacherCourseRepository, ScheduleRepository scheduleRepository, GlobalCacheService globalCache) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.scheduleRepository = scheduleRepository;
        this.globalCache = globalCache;
    }


    @Override
    public List<Student> getStudents(Long teacherId) {
        logger.debug("Fetching students for teacher ID: {}", teacherId);

        if (teacherId == null) {
            logger.warn("Attempted to get students with null teacherId");
            throw new IllegalArgumentException("Invalid teacher ID");
        }

        // No caching for student lists - only individual users and lessons are cached
        List<Lesson> teacherLessons = lessonRepository.findByTeacherId(teacherId);
        List<Student> enrolledStudents = teacherLessons.stream()
                .map(Lesson::getStudent)
                .distinct()
                .collect(Collectors.toList());

        logger.info("Found {} unique students for teacher ID: {} from {} lessons",
                   enrolledStudents.size(), teacherId, teacherLessons.size());
        return enrolledStudents;
    }

    @Override
    public Long getTeacherIdByUsername(String teacherName) {
        logger.debug("Finding teacher ID for username: {}", teacherName);

        if (teacherName == null || teacherName.trim().isEmpty()) {
            logger.warn("Attempted to get teacher ID with null or empty username");
            throw new IllegalArgumentException("Invalid username");
        }

        String cacheKey = "by_username_" + teacherName;

        // Check global cache first for user
        Teacher cached = globalCache.getUser(cacheKey, Teacher.class);
        if (cached != null) {
            logger.debug("Found teacher in cache for username: {}", teacherName);
            logger.info("Returning cached teacher ID: {} for username: {}", cached.getId(), teacherName);
            return cached.getId();
        }

        User user = userRepository.findByUsername(teacherName)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + teacherName));

        // Check if the user is actually a Teacher before casting
        if (!(user instanceof Teacher)) {
            logger.warn("User {} is not a Teacher (found type: {})", teacherName, user.getClass().getSimpleName());
            throw new IllegalArgumentException("User " + teacherName + " is not a teacher");
        }

        Teacher teacher = (Teacher) user;

        // Store user in global cache
        globalCache.putUser(cacheKey, teacher);

        logger.info("Found and cached teacher ID: {} for username: {}", teacher.getId(), teacherName);
        return teacher.getId();
    }

    @Override
    public List<Course> getAvailableCourses() {
        logger.debug("Getting all available courses");
        List<Course> courses = courseRepository.findAll();
        logger.info("Retrieved {} available courses", courses.size());
        return courses;
    }

    @Override
    @Transactional
    public void addTeachableCourse(Long teacherId, Long courseId) {
        logger.debug("Adding teachable course - Teacher ID: {}, Course ID: {}", teacherId, courseId);

        if (teacherCourseRepository.existsByTeacherIdAndCourseId(teacherId, courseId)) {
            logger.debug("Teacher {} already teaches course {}, skipping", teacherId, courseId);
            return;
        }

        var user = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));
        if (!(user instanceof Teacher teacher)) {
            logger.warn("User {} is not a Teacher when adding teachable course", teacherId);
            throw new IllegalArgumentException("User is not a Teacher: " + teacherId);
        }

        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        var tc = new TeacherCourse();
        tc.setTeacher(teacher);
        tc.setCourse(course);
        teacherCourseRepository.save(tc);

        logger.info("Successfully added course '{}' (ID: {}) to teacher '{}' (ID: {})",
                   course.getTitle(), courseId, teacher.getUsername(), teacherId);
    }

    @Override
    @Transactional
    public void removeTeachableCourse(Long teacherId, Long courseId) {
        logger.debug("Removing teachable course - Teacher ID: {}, Course ID: {}", teacherId, courseId);
        teacherCourseRepository.deleteByTeacherIdAndCourseId(teacherId, courseId);
        logger.info("Successfully removed course ID: {} from teacher ID: {}", courseId, teacherId);
    }

    @Override
    public List<Course> getTeachableCourses(Long teacherId) {
        logger.debug("Getting teachable courses for teacher ID: {}", teacherId);
        List<Course> courses = teacherCourseRepository.findByTeacherId(teacherId).stream()
                .map(TeacherCourse::getCourse)
                .toList();
        logger.info("Found {} teachable courses for teacher ID: {}", courses.size(), teacherId);
        return courses;
    }

    @Override
    public List<Lesson> getAcceptedLessons(Long teacherId) {
        logger.debug("Getting accepted lessons for teacher ID: {}", teacherId);
        List<Lesson> lessons = lessonRepository.findByTeacherIdAndStatus(teacherId, "ACCEPTED");
        logger.info("Found {} accepted lessons for teacher ID: {}", lessons.size(), teacherId);
        return lessons;
    }

    @Override
    public List<Lesson> getPendingLessons(Long teacherId) {
        logger.debug("Getting pending lessons for teacher ID: {}", teacherId);
        List<Lesson> lessons = lessonRepository.findByTeacherIdAndStatus(teacherId, "PENDING");
        logger.info("Found {} pending lessons for teacher ID: {}", lessons.size(), teacherId);
        return lessons;
    }

    @Override
    public int countPendingLessons(Long teacherId) {
        logger.debug("Counting pending lessons for teacher ID: {}", teacherId);
        int count = lessonRepository.countByTeacherIdAndStatus(teacherId, "PENDING");
        logger.info("Teacher ID: {} has {} pending lessons", teacherId, count);
        return count;
    }

    @Override
    public Teacher getTeacherProfile(Long teacherId) {
        logger.debug("Getting teacher profile for ID: {}", teacherId);
        String cacheKey = "profile_" + teacherId;

        // Check global cache first for user
        Teacher cached = globalCache.getUser(cacheKey, Teacher.class);
        if (cached != null) {
            logger.debug("Found teacher profile in cache for ID: {}", teacherId);
            logger.info("Returning cached profile for teacher ID: {}", teacherId);
            return cached;
        }

        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null) {
            logger.warn("No user found with ID: {}", teacherId);
            return null;
        }
        if (!(user instanceof Teacher)) {
            logger.warn("User with ID: {} is not a Teacher (type: {})", teacherId, user.getClass().getSimpleName());
            return null;
        }
        Teacher teacher = (Teacher) user;

        // Store user in global cache
        globalCache.putUser(cacheKey, teacher);

        logger.info("Retrieved and cached teacher profile for: {} (ID: {})", teacher.getUsername(), teacherId);
        return teacher;
    }

    @Override
    public Lesson getNextLesson(Long teacherId) {
        logger.debug("Getting next lesson for teacher ID: {}", teacherId);
        String cacheKey = teacherId + "_next";

        // Check global cache first for lesson
        Lesson cached = globalCache.getLesson(cacheKey, Lesson.class);
        if (cached != null) {
            logger.debug("Found next lesson in cache for teacher ID: {}", teacherId);
            logger.info("Returning cached next lesson for teacher ID: {} at {}", teacherId, cached.getTimestamp());
            return cached;
        }

        List<Lesson> upcomingLessons = lessonRepository.findUpcomingByTeacher(teacherId, LocalDateTime.now());
        Lesson nextLesson = upcomingLessons.stream().findFirst().orElse(null);

        // Store lesson in global cache
        if (nextLesson != null) {
            globalCache.putLesson(cacheKey, nextLesson);
            // Add null check for student
            String studentInfo = nextLesson.getStudent() != null ? nextLesson.getStudent().getUsername() : "Unknown Student";
            logger.info("Found and cached next lesson for teacher ID: {} at {} with student: {}",
                       teacherId, nextLesson.getTimestamp(), studentInfo);
        } else {
            logger.info("No upcoming lessons found for teacher ID: {}", teacherId);
        }

        return nextLesson;
    }

    @Override
    public int getLessonCount(Long teacherId) {
        logger.debug("Getting lesson count for teacher ID: {}", teacherId);
        int count = lessonRepository.countByTeacherId(teacherId);
        logger.info("Teacher ID: {} has {} total lessons", teacherId, count);
        return count;
    }

    @Override
    public List<Teacher> findTeachersByCourse(Long courseId) {
        logger.debug("Finding teachers for course ID: {}", courseId);
        if (courseId == null) {
            logger.warn("Attempted to find teachers with null courseId");
            throw new IllegalArgumentException("courseId is required");
        }
        List<Teacher> teachers = teacherCourseRepository.findTeachersByCourseId(courseId);
        logger.info("Found {} teachers for course ID: {}", teachers.size(), courseId);
        return teachers;
    }

    @Override
    public void acceptLesson(Long lessonId) {
        logger.debug("Accepting lesson with ID: {}", lessonId);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));
        lesson.setStatus("ACCEPTED");
        lessonRepository.save(lesson);

        // Add null check for student
        String studentInfo = lesson.getStudent() != null ? lesson.getStudent().getUsername() : "Unknown Student";
        logger.info("Successfully accepted lesson ID: {} for student: {} at {}",
                   lessonId, studentInfo, lesson.getTimestamp());
    }

    @Override
    public List<Schedule> getTeacherSchedule(Long teacherId) {
        logger.debug("Getting teacher schedule for ID: {}", teacherId);
        List<Schedule> schedule = scheduleRepository.findByTeacherIdOrderByScheduledTimeAsc(teacherId);
        logger.info("Retrieved {} schedule entries for teacher ID: {}", schedule.size(), teacherId);
        return schedule;
    }

    @Override
    public List<Schedule> getUpcomingSchedule(Long teacherId) {
        logger.debug("Getting upcoming schedule for teacher ID: {}", teacherId);
        List<Schedule> upcomingSchedule = scheduleRepository.findByTeacherIdAndScheduledTimeAfterOrderByScheduledTimeAsc(
                teacherId, LocalDateTime.now());
        logger.info("Found {} upcoming schedule entries for teacher ID: {}", upcomingSchedule.size(), teacherId);
        return upcomingSchedule;
    }

    @Override
    public List<Schedule> getSchedulesForMonth(Long teacherId, LocalDateTime start, LocalDateTime end) {
        logger.debug("Getting schedules for teacher ID: {} between {} and {}", teacherId, start, end);
        // Get lessons for the month and convert to schedules
        List<Lesson> lessons = lessonRepository.findByTeacherIdAndTimestampBetweenOrderByTimestampAsc(
                teacherId, start, end);

        // Convert lessons to schedules for display
        List<Schedule> schedules = ScheduleUtils.convertLessonsToSchedules(lessons);
        logger.info("Converted {} lessons to {} schedule entries for teacher ID: {} (period: {} to {})",
                   lessons.size(), schedules.size(), teacherId, start, end);
        return schedules;
    }

    @Override
    public List<Lesson> getUpcomingLessonsForTeacher(Long teacherId) {
        logger.debug("Getting upcoming lessons for teacher ID: {}", teacherId);
        List<Lesson> lessons = lessonRepository.findUpcomingByTeacher(teacherId, LocalDateTime.now());
        logger.info("Found {} upcoming lessons for teacher ID: {}", lessons.size(), teacherId);
        return lessons;
    }

    @Override
    public List<Lesson> getPastLessonsForTeacher(Long teacherId) {
        logger.debug("Getting past lessons for teacher ID: {}", teacherId);
        List<Lesson> lessons = lessonRepository.findPastByTeacher(teacherId, LocalDateTime.now());
        logger.info("Found {} past lessons for teacher ID: {}", lessons.size(), teacherId);
        return lessons;
    }

    @Override
    public List<Lesson> getLessonsWithStudent(Long teacherId, Long studentId) {
        logger.debug("Getting lessons between teacher ID: {} and student ID: {}", teacherId, studentId);
        List<Lesson> lessons = lessonRepository.findByTeacherIdAndStudentIdOrderByTimestampDesc(teacherId, studentId);
        logger.info("Found {} lessons between teacher ID: {} and student ID: {}", lessons.size(), teacherId, studentId);
        return lessons;
    }
}
