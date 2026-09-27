package com.example.mini_project.models;

import lombok.Data;

@Data
public class PaymentWebhookRequest {

    private int orderId;

    private boolean paid;
}