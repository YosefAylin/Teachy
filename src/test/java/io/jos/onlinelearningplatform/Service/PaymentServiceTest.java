package io.jos.onlinelearningplatform.service;

import io.jos.onlinelearningplatform.repository.PaymentRepository;
import io.jos.onlinelearningplatform.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class PaymentServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceTest.class);

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(paymentRepository);
    }

    @Test
    void processPayment_ValidInput_Success() {
        // Arrange
        Long studentId = 1L;
        Long teacherId = 2L;
        double amount = 100.0;

        // Act & Assert
        assertDoesNotThrow(() -> {
            paymentService.processPayment(studentId, teacherId, amount);
        });
        logger.info("Successfully tested payment processing");
    }

    @Test
    void processPayment_InvalidAmount_ThrowsException() {
        // Arrange
        Long studentId = 1L;
        Long teacherId = 2L;
        double invalidAmount = -100.0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(studentId, teacherId, invalidAmount);
        });
        logger.info("Successfully tested payment processing with invalid amount");
    }
}
