package jos.onlinelearningplatform.repository;

import jos.onlinelearningplatform.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
