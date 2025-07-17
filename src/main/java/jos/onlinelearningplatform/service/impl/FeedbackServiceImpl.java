package jos.onlinelearningplatform.service.impl;

public interface FeedbackServiceImpl {
    void submitFeedback(Long userId, Long courseId, String feedbackText);
    void viewFeedbackForCourse(Long courseId);
    void viewFeedbackForUser(Long userId);
    void deleteFeedback(Long feedbackId);
    void updateFeedback(Long feedbackId, String newFeedbackText);

}
