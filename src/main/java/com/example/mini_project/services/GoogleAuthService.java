package com.example.mini_project.services;

import com.example.mini_project.auth.JwtService;
import com.example.mini_project.models.LoginResponse;
import com.example.mini_project.models.Image;
import com.example.mini_project.models.User;
import com.example.mini_project.repositories.ImageRepository;
import com.example.mini_project.repositories.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.UUID;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.client-id}")
    private String googleClientId;

    public GoogleAuthService(
            UserRepository userRepository,
            ImageRepository imageRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse loginWithGoogle(
            String idTokenString
    ) {

        try {

            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(),
                            GsonFactory.getDefaultInstance()
                    )
                            .setAudience(
                                    Collections.singletonList(
                                            googleClientId
                                    )
                            )
                            .build();

            GoogleIdToken idToken =
                    verifier.verify(idTokenString);

            if (idToken == null) {

                throw new RuntimeException(
                        "Invalid Google ID token"
                );
            }

            GoogleIdToken.Payload payload =
                    idToken.getPayload();

            String email =
                    payload.getEmail();

            String name =
                    (String) payload.get("name");

            String pictureUrl =
                    (String) payload.get("picture");

            if (email == null ||
                    email.isBlank()) {

                throw new RuntimeException(
                        "Google account has no email"
                );
            }

            System.out.println(
                    "GOOGLE EMAIL: " + email
            );

            System.out.println(
                    "GOOGLE NAME: " + name
            );

            User user =
                    userRepository.findByEmail(email);

            // =========================
            // CREATE USER
            // =========================

            if (user == null) {

                user = new User();

                user.setName(
                        name != null &&
                                !name.isBlank()
                                ? name
                                : email.split("@")[0]
                );

                user.setEmail(email);

                user.setPassword(
                        passwordEncoder.encode(
                                UUID.randomUUID().toString()
                        )
                );

                user =
                        userRepository.save(user);
            }

            // =========================
            // SAVE GOOGLE PROFILE IMAGE
            // =========================

            if (pictureUrl != null &&
                    !pictureUrl.isBlank()) {

                try {

                    URL url =
                            new URL(pictureUrl);

                    try (InputStream inputStream =
                                 url.openStream()) {

                        ByteArrayOutputStream outputStream =
                                new ByteArrayOutputStream();

                        byte[] buffer =
                                new byte[4096];

                        int bytesRead;

                        while ((bytesRead =
                                inputStream.read(buffer)) != -1) {

                            outputStream.write(
                                    buffer,
                                    0,
                                    bytesRead
                            );
                        }

                        byte[] imageData =
                                outputStream.toByteArray();

                        if (imageData.length > 0) {

                            Image image =
                                    new Image();

                            image.setName(
                                    "google-profile-" +
                                            user.getId() +
                                            ".jpg"
                            );

                            image.setType(
                                    "image/jpeg"
                            );

                            image.setData(
                                    imageData
                            );

                            image =
                                    imageRepository.save(
                                            image
                                    );

                            user.setImageId(
                                    image.getId()
                            );

                            user =
                                    userRepository.save(
                                            user
                                    );
                        }
                    }

                } catch (Exception imageException) {

                    System.out.println(
                            "GOOGLE IMAGE ERROR: " +
                                    imageException.getMessage()
                    );
                }
            }

            // =========================
            // CREATE JWT
            // =========================

            String token =
                    jwtService.generateToken(
                            user.getId(),
                            user.getEmail()
                    );

            return new LoginResponse(
                    user.getId(),
                    token
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Google authentication failed",
                    e
            );
        }
    }
}