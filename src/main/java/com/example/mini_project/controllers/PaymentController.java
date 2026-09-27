package com.example.mini_project.controllers;

import com.example.mini_project.models.PaymentRequest;
import com.example.mini_project.models.PaymentResponse;
import com.example.mini_project.models.PaymentStatusResponse;
import com.example.mini_project.services.PaymentService;
import com.stripe.exception.StripeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    // ============================================================
    // CREATE CHECKOUT SESSION
    // ============================================================

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(
            @RequestBody PaymentRequest request
    ) {

        try {

            String checkoutUrl =
                    paymentService.createCheckoutSession(
                            request.getCartId()
                    );

            return ResponseEntity.ok(
                    new PaymentResponse(checkoutUrl)
            );

        } catch (StripeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ============================================================
    // CHECK STRIPE PAYMENT STATUS
    // ============================================================

    @GetMapping("/status/{sessionId}")
    public ResponseEntity<?> getPaymentStatus(
            @PathVariable String sessionId
    ) {

        try {

            String status =
                    paymentService.getPaymentStatus(
                            sessionId
                    );

            Integer orderId =
                    paymentService.getOrderId(
                            sessionId
                    );

            return ResponseEntity.ok(
                    new PaymentStatusResponse(
                            status,
                            orderId
                    )
            );

        } catch (StripeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}