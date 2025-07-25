package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
