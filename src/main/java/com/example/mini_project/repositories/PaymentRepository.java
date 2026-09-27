package com.example.mini_project.repositories;

import com.example.mini_project.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findByStripeSessionId(String stripeSessionId);
}