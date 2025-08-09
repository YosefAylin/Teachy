package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("""
    select l
      from Lesson l
     where l.course.id in :courseIds
       and l.date > :from
    """)
    List<Lesson> findUpcomingLessonsByCourseIds(
            @Param("courseIds") List<Long> courseIds,
            @Param("from")       LocalDateTime from
    );

    // LessonRepository.java
    @Query("""
        select l
        from Lesson l
        where l.teacher.id = :teacherId
        order by l.timestamp asc
    """)
    List<Lesson> findAllByTeacherIdOrderByTimestamp(@Param("teacherId") Long teacherId);

    @Query("""
       select l
         from Lesson l
        where l.teacher.id = :teacherId
          and l.timestamp >= :now
     order by l.timestamp asc
    """)
    List<Lesson> findUpcomingByTeacher(@Param("teacherId") Long teacherId,
                                       @Param("now") java.time.LocalDateTime now);

    int countByTeacherId(Long teacherId);

    List<Lesson> findByTeacherId(Long teacherId);
    List<Lesson> findByTeacherIdAndStatus(Long teacherId, String status);
    int countByTeacherIdAndStatus(Long teacherId, String status);
    List<Lesson> findByStudent_IdAndDateLessThanAndStatusInOrderByDateDesc(Long studentId, LocalDate today, List<String> accepted);
    List<Lesson> findByStudent_IdAndStatusInAndDateGreaterThanEqualOrderByDateAsc(Long studentId, List<String> statuses, LocalDate now);

    // All lessons for a student (latest first)
    List<Lesson> findByStudent_IdOrderByDateDesc(Long studentId);

    // Upcoming for student (today or later) with status PENDING/ACCEPTED
    List<Lesson> findByStudent_IdAndStatusInAndDateGreaterThanEqualOrderByDateAsc(
            Long studentId, Collection<String> statuses, LocalDate from);

    // Past for student (before today) with selected statuses
    List<Lesson> findByStudent_IdAndStatusInAndDateLessThanOrderByDateDesc(
            Long studentId, Collection<String> statuses, LocalDate before);

    // Convenience: all REJECTED for student (any date)
    List<Lesson> findByStudent_IdAndStatusOrderByDateDesc(Long studentId, String status);
}

