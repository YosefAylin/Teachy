package io.jos.onlinelearningplatform.repository;

import io.jos.onlinelearningplatform.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
