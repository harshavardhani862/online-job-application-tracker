package com.klu.jobtracker.repository;

import com.klu.jobtracker.entity.Payment;
import com.klu.jobtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserOrderByCreatedAtDesc(User user);
    Optional<Payment> findByRazorpayOrderId(String orderId);
}
