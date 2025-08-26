package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l FROM Lesson l WHERE l.teacher.id = :teacherId AND l.timestamp >= :now ORDER BY l.timestamp ASC")
    List<Lesson> findUpcomingByTeacher(@Param("teacherId") Long teacherId,
                                       @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.teacher.id = :teacherId")
    int countByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT l FROM Lesson l WHERE l.teacher.id = :teacherId")
    List<Lesson> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT l FROM Lesson l WHERE l.teacher.id = :teacherId AND l.status = :status")
    List<Lesson> findByTeacherIdAndStatus(@Param("teacherId") Long teacherId, @Param("status") String status);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.teacher.id = :teacherId AND l.status = :status")
    int countByTeacherIdAndStatus(@Param("teacherId") Long teacherId, @Param("status") String status);

    @Query("SELECT l FROM Lesson l WHERE l.student.id = :studentId AND l.status IN :statuses AND l.timestamp >= :from ORDER BY l.timestamp ASC")
    List<Lesson> findByStudent_IdAndStatusInAndTimestampGreaterThanEqualOrderByTimestampAsc(
            @Param("studentId") Long studentId, @Param("statuses") Collection<String> statuses, @Param("from") LocalDateTime from);

    @Query("SELECT l FROM Lesson l WHERE l.student.id = :studentId AND l.status IN :statuses AND l.timestamp < :before ORDER BY l.timestamp DESC")
    List<Lesson> findByStudent_IdAndStatusInAndTimestampLessThanOrderByTimestampDesc(
            @Param("studentId") Long studentId, @Param("statuses") Collection<String> statuses, @Param("before") LocalDateTime before);

    @Query("SELECT l FROM Lesson l WHERE l.student.id = :studentId AND l.status = :status ORDER BY l.timestamp DESC")
    List<Lesson> findByStudent_IdAndStatusOrderByTimestampDesc(@Param("studentId") Long studentId, @Param("status") String status);

    @Query("SELECT l FROM Lesson l WHERE l.teacher.id = :teacherId AND l.timestamp BETWEEN :start AND :end ORDER BY l.timestamp ASC")
    List<Lesson> findByTeacherIdAndTimestampBetweenOrderByTimestampAsc(@Param("teacherId") Long teacherId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT l FROM Lesson l WHERE l.student.id = :studentId AND l.timestamp BETWEEN :start AND :end ORDER BY l.timestamp ASC")
    List<Lesson> findByStudentIdAndTimestampBetweenOrderByTimestampAsc(@Param("studentId") Long studentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT l FROM Lesson l WHERE l.student.id = :studentId AND l.timestamp > :now ORDER BY l.timestamp ASC")
    List<Lesson> findUpcomingByStudent(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);

    @Query("SELECT l FROM Lesson l WHERE l.teacher.id = :teacherId AND l.timestamp < :now ORDER BY l.timestamp DESC")
    List<Lesson> findPastByTeacher(@Param("teacherId") Long teacherId, @Param("now") LocalDateTime now);

    @Query("SELECT l FROM Lesson l WHERE l.course.id = :courseId ORDER BY l.timestamp DESC")
    List<Lesson> findByCourseIdOrderByTimestampDesc(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT l FROM Lesson l WHERE l.teacher.id = :teacherId AND l.student.id = :studentId ORDER BY l.timestamp DESC")
    List<Lesson> findByTeacherIdAndStudentIdOrderByTimestampDesc(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);
}
