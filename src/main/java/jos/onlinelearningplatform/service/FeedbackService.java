package jos.onlinelearningplatform.service;

public interface FeedbackService {
    void submitFeedback(Long userId, Long courseId, String feedbackText);
    void viewFeedbackForCourse(Long courseId);
    void viewFeedbackForUser(Long userId);
    void deleteFeedback(Long feedbackId);
    void updateFeedback(Long feedbackId, String newFeedbackText);

}
