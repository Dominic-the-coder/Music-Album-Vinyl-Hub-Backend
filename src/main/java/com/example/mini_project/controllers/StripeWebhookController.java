package com.example.mini_project.controllers;

import com.example.mini_project.services.PaymentService;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class StripeWebhookController {

    @Autowired
    private PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {

        Event event;

        try {

            event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    webhookSecret
            );

        } catch (SignatureVerificationException e) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid Stripe signature");
        }

        if ("checkout.session.completed"
                .equals(event.getType())) {

            Session session =
                    (Session) event
                            .getDataObjectDeserializer()
                            .getObject()
                            .orElse(null);

            if (session != null) {

                paymentService.markPaymentAsPaid(
                        session.getId()
                );
            }
        }

        return ResponseEntity.ok("Webhook received");
    }
}