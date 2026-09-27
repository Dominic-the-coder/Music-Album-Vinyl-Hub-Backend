package com.example.mini_project.models;

import lombok.Data;

@Data
public class PaymentResponse {

    private String checkoutUrl;

    public PaymentResponse(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
}