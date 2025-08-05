package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("""
    select l
      from Lesson l
     where l.course.id in :courseIds
       and l.startDate > :from
    """)
    List<Lesson> findUpcomingLessonsByCourseIds(
            @Param("courseIds") List<Long> courseIds,
            @Param("from")       LocalDateTime from
    );}
