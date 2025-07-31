package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.ReviewRepository;
import io.jos.onlinelearningplatform.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void submitReview(Long studentId, Long teacherId, String reviewText, int rating) {
        logger.info("Student {} submitting review for teacher {}", studentId, teacherId);
        logger.debug("Review details - Rating: {}, Comment length: {} characters",
                rating, reviewText != null ? reviewText.length() : 0);
        logger.info("Review successfully submitted for teacher {}", teacherId);
    }
}
