package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.PaymentRepository;
import io.jos.onlinelearningplatform.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void processPayment(Long userId, Long courseId, double amount) {

    }

    @Override
    public void refundPayment(Long userId, Long courseId, double amount) {

    }

    @Override
    public void viewPaymentHistory(Long userId) {

    }

}