package io.jos.onlinelearningplatform.service;

public interface ReviewService {
    void submitReview(Long studentId, Long teacherId, String reviewText, int rating);
    void deleteReview(Long reviewId);
    void updateReview(Long reviewId, String newReviewText, int newRating);
}
