package jos.onlinelearningplatform.repository;

import jos.onlinelearningplatform.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
