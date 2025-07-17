package jos.onlinelearningplatform.service;

public interface ReviewService {
    void submitReview(Long userId, Long courseId, String reviewText, int rating);
    void viewReviewsForCourse(Long courseId);
    void viewReviewsByUser(Long userId);
    void deleteReview(Long reviewId);
    void updateReview(Long reviewId, String newReviewText, int newRating);
}
