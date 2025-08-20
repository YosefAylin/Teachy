package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.model.*;
import io.jos.onlinelearningplatform.repository.*;
import io.jos.onlinelearningplatform.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    public TeacherServiceImpl(UserRepository userRepository, CourseRepository courseRepository, LessonRepository lessonRepository, PasswordEncoder passwordEncoder, TeacherCourseRepository teacherCourseRepository, ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void handleBooking(Long lessonId) {
        logger.info("Teacher handling booking request for lesson ID: {}", lessonId);
        if (lessonId == null) {
            logger.warn("Attempted to handle booking with null lessonId");
            throw new IllegalArgumentException("Invalid lesson ID");
        }

        logger.info("Successfully handled booking for lesson {}", lessonId);
    }



    @Override
    public List<Lesson> getUpcomingLessons(Long teacherId) {
        logger.info("Fetching upcoming lessons for teacher ID: {}", teacherId);

        if (teacherId == null) {
            logger.warn("Attempted to get lessons with null teacherId");
            throw new IllegalArgumentException("Invalid teacher ID");
        }

        // Get teacher's courses
        List<Course> teacherCourses = getTeachableCourses(teacherId);

        // Get upcoming lessons for all teacher's courses
        List<Lesson> upcomingLessons = lessonRepository.findUpcomingLessonsByCourseIds(
                teacherCourses.stream()
                        .map(Course::getId)
                        .collect(Collectors.toList()),
                LocalDateTime.now()
        );

        logger.info("Found {} upcoming lessons for teacher ID: {}", upcomingLessons.size(), teacherId);
        return upcomingLessons;
    }

    @Override
    public List<Student> getStudents(Long teacherId) {
        logger.info("Fetching students for teacher ID: {}", teacherId);

        if (teacherId == null) {
            logger.warn("Attempted to get students with null teacherId");
            throw new IllegalArgumentException("Invalid teacher ID");
        }

        // Get lessons for this teacher
        List<Lesson> teacherLessons = lessonRepository.findByTeacherId(teacherId);

        // Extract unique students from one-on-one lessons
        List<Student> enrolledStudents = teacherLessons.stream()
                .map(Lesson::getStudent)
                .distinct()
                .collect(Collectors.toList());

        logger.info("Found {} unique students for teacher ID: {}", enrolledStudents.size(), teacherId);
        return enrolledStudents;
    }

    @Override
    public Long getTeacherIdByUsername(String teacherName) {
        logger.info("Finding teacher ID for username: {}", teacherName);

        if (teacherName == null || teacherName.trim().isEmpty()) {
            logger.warn("Attempted to get teacher ID with null or empty username");
            throw new IllegalArgumentException("Invalid username");
        }

        Teacher teacher = (Teacher) userRepository.findByUsername(teacherName)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with username: " + teacherName));

        logger.info("Found teacher ID: {} for username: {}", teacher.getId(), teacherName);
        return teacher.getId();
    }

    @Override
    public List<Course> getAvailableCourses() {
        return courseRepository.findAll();
    }

    @Override
    @Transactional
    public void addTeachableCourse(Long teacherId, Long courseId) {
        if (teacherCourseRepository.existsByTeacherIdAndCourseId(teacherId, courseId)) return;

        var user = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));
        if (!(user instanceof Teacher teacher)) {
            throw new IllegalArgumentException("User is not a Teacher: " + teacherId);
        }

        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        var tc = new TeacherCourse();
        tc.setTeacher(teacher);
        tc.setCourse(course);
        teacherCourseRepository.save(tc);
    }

    @Override
    @Transactional
    public void removeTeachableCourse(Long teacherId, Long courseId) {
        teacherCourseRepository.deleteByTeacherIdAndCourseId(teacherId, courseId);
    }

    @Override
    public List<Course> getTeachableCourses(Long teacherId) {
        return teacherCourseRepository.findByTeacherId(teacherId).stream()
                .map(TeacherCourse::getCourse)
                .toList();
    }

    @Override
    public List<Lesson> getAcceptedLessons(Long teacherId) {
        return lessonRepository.findByTeacherIdAndStatus(teacherId, "ACCEPTED");

    }

    @Override
    public List<Lesson> getPendingLessons(Long teacherId) {
        return lessonRepository.findByTeacherIdAndStatus(teacherId,"PENDING");
    }

    @Override
    public int countPendingLessons(Long teacherId) {
        return lessonRepository.countByTeacherIdAndStatus(teacherId, "PENDING");
    }

    @Override
    public Teacher getTeacherProfile(Long teacherId) {
        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null) {
            logger.error("No user found with ID: {}", teacherId);
            return null;
        }
        if (!(user instanceof Teacher)) {
            logger.error("User with ID: {} is not a Teacher", teacherId);
            return null;
        }
        return (Teacher) user;
    }

    @Override
    public List<Lesson> getAllLessonsOrdered(Long teacherId) {
        return lessonRepository.findAllByTeacherIdOrderByTimestamp(teacherId);
    }

    @Override
    public Lesson getNextLesson(Long teacherId) {
        return lessonRepository.findUpcomingByTeacher(teacherId, LocalDateTime.now())
                .stream().findFirst().orElse(null);
    }

    @Override
    public int getLessonCount(Long teacherId) {
        return lessonRepository.countByTeacherId(teacherId);
    }

    @Override
    public List<Teacher> findTeachersByCourse(Long courseId) {
        if (courseId == null) throw new IllegalArgumentException("courseId is required");
        return teacherCourseRepository.findTeachersByCourseId(courseId);
    }

    @Override
    public void acceptLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));
        lesson.setStatus("ACCEPTED");
        lessonRepository.save(lesson);
    }

    @Override
    public List<Schedule> getTeacherSchedule(Long teacherId) {
        return scheduleRepository.findByTeacherIdOrderByScheduledTimeAsc(teacherId);
    }

    @Override
    public List<Schedule> getUpcomingSchedule(Long teacherId) {
        return scheduleRepository.findByTeacherIdAndScheduledTimeAfterOrderByScheduledTimeAsc(
                teacherId, LocalDateTime.now());
    }

    @Override
    public List<Schedule> getSchedulesForMonth(Long teacherId, LocalDateTime start, LocalDateTime end) {
        // Get lessons for the month and convert to schedules
        List<Lesson> lessons = lessonRepository.findByTeacherIdAndTimestampBetweenOrderByTimestampAsc(
                teacherId, start, end);

        // Convert lessons to schedules for display
        return lessons.stream()
                .map(lesson -> {
                    Schedule schedule = new Schedule();
                    schedule.setTeacher(lesson.getTeacher());
                    schedule.setStudent(lesson.getStudent());
                    schedule.setLesson(lesson);
                    schedule.setScheduledTime(lesson.getTimestamp());
                    schedule.setStatus(lesson.getStatus());
                    return schedule;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Lesson> getUpcomingLessonsForTeacher(Long teacherId) {
        return lessonRepository.findUpcomingByTeacher(teacherId, LocalDateTime.now());
    }

    @Override
    public List<Lesson> getPastLessonsForTeacher(Long teacherId) {
        return lessonRepository.findPastByTeacher(teacherId, LocalDateTime.now());
    }
}

