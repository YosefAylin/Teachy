package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.teacher.id = :teacherId ORDER BY s.scheduledTime ASC")
    List<Schedule> findByTeacherIdOrderByScheduledTimeAsc(@Param("teacherId") Long teacherId);

    @Query("SELECT s FROM Schedule s WHERE s.teacher.id = :teacherId AND s.scheduledTime > :time ORDER BY s.scheduledTime ASC")
    List<Schedule> findByTeacherIdAndScheduledTimeAfterOrderByScheduledTimeAsc(@Param("teacherId") Long teacherId, @Param("time") LocalDateTime time);

    @Query("SELECT s FROM Schedule s WHERE s.student.id = :studentId ORDER BY s.scheduledTime ASC")
    List<Schedule> findByStudentIdOrderByScheduledTimeAsc(@Param("studentId") Long studentId);

    @Query("SELECT s FROM Schedule s WHERE s.student.id = :studentId AND s.scheduledTime > :now ORDER BY s.scheduledTime ASC")
    List<Schedule> findByStudentIdAndScheduledTimeAfterOrderByScheduledTimeAsc(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);
}
