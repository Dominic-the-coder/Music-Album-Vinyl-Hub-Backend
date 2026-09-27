package com.example.mini_project.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentStatusResponse {

    private String paymentStatus;

    private Integer orderId;
}