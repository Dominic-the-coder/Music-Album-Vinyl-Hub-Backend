package com.example.mini_project.controllers;

import com.example.mini_project.models.GoogleLoginRequest;
import com.example.mini_project.models.LoginResponse;
import com.example.mini_project.services.GoogleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    public GoogleAuthController(
            GoogleAuthService googleAuthService
    ) {
        this.googleAuthService = googleAuthService;
    }

    @PostMapping("/auth/google")
    public ResponseEntity<?> googleLogin(
            @RequestBody GoogleLoginRequest request
    ) {

        try {

            if (request.getIdToken() == null ||
                    request.getIdToken().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Google ID token is required"
                                )
                        );
            }

            LoginResponse response =
                    googleAuthService.loginWithGoogle(
                            request.getIdToken()
                    );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Google authentication failed"
                            )
                    );
        }
    }
}