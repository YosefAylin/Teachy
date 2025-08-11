package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("""
    select l
      from Lesson l
     where l.course.id in :courseIds
       and l.timestamp > :from
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
    List<Lesson> findByStudent_IdAndTimestampLessThanAndStatusInOrderByTimestampDesc(Long studentId, LocalDateTime today, List<String> accepted);
    List<Lesson> findByStudent_IdAndStatusInAndTimestampGreaterThanEqualOrderByTimestampAsc(Long studentId, List<String> statuses, LocalDateTime now);

    // All lessons for a student (latest first)
    List<Lesson> findByStudent_IdOrderByTimestampDesc(Long studentId);

    // Upcoming for student (today or later) with status PENDING/ACCEPTED
    List<Lesson> findByStudent_IdAndStatusInAndTimestampGreaterThanEqualOrderByTimestampAsc(
            Long studentId, Collection<String> statuses, LocalDateTime from);

    // Past for student (before today) with selected statuses
    List<Lesson> findByStudent_IdAndStatusInAndTimestampLessThanOrderByTimestampDesc(
            Long studentId, Collection<String> statuses, LocalDateTime before);

    // Convenience: all REJECTED for student (any date)
    List<Lesson> findByStudent_IdAndStatusOrderByTimestampDesc(Long studentId, String status);

}

