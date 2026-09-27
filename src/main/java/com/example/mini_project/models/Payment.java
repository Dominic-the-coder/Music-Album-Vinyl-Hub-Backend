package com.example.mini_project.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "cart_id", nullable = false)
    private int cartId;

    @Column(name = "payment_type", nullable = false)
    private String paymentType;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "stripe_session_id", unique = true)
    private String stripeSessionId;

    @Column(name = "order_id")
    private Integer orderId;
}