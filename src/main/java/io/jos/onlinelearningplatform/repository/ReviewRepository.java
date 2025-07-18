package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
