package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface StudentService {
    List<Schedule> getStudentSchedule(Long studentId);
    List<Schedule> getUpcomingSchedule(Long studentId);
    List<Schedule> getSchedulesForMonth(Long studentId, LocalDateTime start, LocalDateTime end);
    Lesson getNextLesson(Long studentId);
}
