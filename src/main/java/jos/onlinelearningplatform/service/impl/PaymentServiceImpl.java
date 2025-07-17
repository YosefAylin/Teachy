package jos.onlinelearningplatform.service.impl;

public interface PaymentServiceImpl {
    void processPayment(Long userId, Long courseId, double amount);
    void refundPayment(Long userId, Long courseId, double amount);
    void viewPaymentHistory(Long userId);
    void updatePaymentDetails(Long userId, String paymentMethod);
    boolean validatePayment(Long userId, Long courseId, double amount);
}
