package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.Course;
import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Schedule;
import io.jos.onlinelearningplatform.model.Student;
import io.jos.onlinelearningplatform.model.Teacher;

import java.time.LocalDateTime;
import java.util.List;

public interface TeacherService {
    List<Student> getStudents(Long teacherId);
    Long getTeacherIdByUsername(String teacherName);
    List<Course> getAvailableCourses();
    void addTeachableCourse(Long teacherId, Long courseId);
    void removeTeachableCourse(Long teacherId, Long courseId);
    List<Course> getTeachableCourses(Long teacherId);
    List<Lesson> getAcceptedLessons(Long teacherId);
    List<Lesson> getPendingLessons(Long teacherId);
    int countPendingLessons(Long teacherId);
    Teacher getTeacherProfile(Long teacherId);
    Lesson getNextLesson(Long teacherId);
    int getLessonCount(Long teacherId);
    List<Teacher> findTeachersByCourse(Long courseId);
    void acceptLesson(Long lessonId);
    List<Schedule> getTeacherSchedule(Long teacherId);
    List<Schedule> getUpcomingSchedule(Long teacherId);
    List<Schedule> getSchedulesForMonth(Long teacherId, LocalDateTime start, LocalDateTime end);
    List<Lesson> getUpcomingLessonsForTeacher(Long teacherId);
    List<Lesson> getPastLessonsForTeacher(Long teacherId);
    List<Lesson> getLessonsWithStudent(Long teacherId, Long studentId);
}
