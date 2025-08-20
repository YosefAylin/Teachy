package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByTeacherIdOrderByScheduledTimeAsc(Long teacherId);

    List<Schedule> findByTeacherIdAndScheduledTimeAfterOrderByScheduledTimeAsc(Long teacherId, LocalDateTime time);

    List<Schedule> findByStudentIdOrderByScheduledTimeAsc(Long studentId);

    List<Schedule> findByStudentIdAndScheduledTimeAfterOrderByScheduledTimeAsc(Long studentId, LocalDateTime now);

    List<Schedule> findByTeacherIdAndScheduledTimeBetweenOrderByScheduledTimeAsc(Long teacherId, LocalDateTime start, LocalDateTime end);

    List<Schedule> findByStudentIdAndScheduledTimeBetweenOrderByScheduledTimeAsc(Long studentId, LocalDateTime start, LocalDateTime end);
}


