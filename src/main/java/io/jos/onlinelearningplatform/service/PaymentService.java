package io.jos.onlinelearningplatform.service;

public interface PaymentService {
    void processPayment(Long studentId, Long teacherId, double amount);
    void refundPayment(Long studentId, Long teacherId, double amount);
    void viewPaymentHistory(Long userId);
}
