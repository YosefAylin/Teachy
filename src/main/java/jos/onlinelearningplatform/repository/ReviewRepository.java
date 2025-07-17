package jos.onlinelearningplatform.repository;

import jos.onlinelearningplatform.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
