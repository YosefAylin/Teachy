package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.ReviewRepository;
import io.jos.onlinelearningplatform.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void submitReview(Long userId, Long courseId, String reviewText, int rating) {

    }

//    @Override
//    public void viewReviewsForTeacher(Long userId) {
//
//    }
//
//    @Override
//    public void viewReviewsByStudent(Long userId) {
//
//    }
//
//    @Override
//    public void deleteReview(Long reviewId) {
//
//    }
//
//    @Override
//    public void updateReview(Long reviewId, String newReviewText, int newRating) {
//
//    }
}
