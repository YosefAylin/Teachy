package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.lesson.id = :lessonId ORDER BY m.sentAt ASC")
    List<Message> findByLessonIdOrderBySentAtAsc(@Param("lessonId") Long lessonId);
}