package io.jos.onlinelearningplatform.service.impl;

import io.jos.onlinelearningplatform.repository.PaymentRepository;
import io.jos.onlinelearningplatform.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void processPayment(Long studentId, Long teacherId, double amount) {
        logger.info("Processing payment of {} from student {} to teacher {}",
            amount, studentId, teacherId);


        logger.debug("Payment transaction details: Amount={}, StudentId={}, TeacherId={}",
            amount, studentId, teacherId);
    }

    @Override
    public void viewPaymentHistory(Long userId) {

    }

}