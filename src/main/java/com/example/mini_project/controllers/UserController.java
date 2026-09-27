package com.example.mini_project.controllers;

import com.example.mini_project.models.ChangePasswordRequest;
import com.example.mini_project.models.RegisterRequest;
import com.example.mini_project.models.RegisterResponse;
import com.example.mini_project.models.User;
import com.example.mini_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService service;


    // GET ALL USERS
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }


    // GET USER BY ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable int id
    ) {

        User user =
                service.getUserById(id);

        if (user == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(user);
    }


    // GET USER BY EMAIL
    @GetMapping("/users/email")
    public ResponseEntity<User> getUserByEmail(
            @RequestParam String email
    ) {

        User user =
                service.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(user);
    }


    // REGISTER
    @PostMapping("/users/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {

        User user =
                service.register(
                        request.getName(),
                        request.getEmail(),
                        request.getPassword()
                );

        return ResponseEntity.ok(
                new RegisterResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        "Registration successful"
                )
        );
    }


    // CREATE USER
    @PostMapping("/users")
    public ResponseEntity<User> saveUser(
            @RequestBody User user
    ) {

        User savedUser =
                service.saveUser(user);

        return ResponseEntity.ok(savedUser);
    }


    // UPDATE USER
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @RequestBody User updatedUser
    ) {

        User user =
                service.updateUser(
                        id,
                        updatedUser
                );

        if (user == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(user);
    }


    // CHANGE PASSWORD
    @PutMapping("/users/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Integer id,
            @RequestBody ChangePasswordRequest request
    ) {

        User user =
                service.changePassword(
                        id,
                        request.getCurrentPassword(),
                        request.getNewPassword()
                );

        if (user == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Current password is incorrect"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password changed successfully"
                )
        );
    }

    @PutMapping("/users/{id}/image/{imageId}")
    public ResponseEntity<User> updateProfileImage(
            @PathVariable Integer id,
            @PathVariable Integer imageId
    ) {

        User user = service.updateProfileImage(id, imageId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }


    // DELETE USER
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable int id
    ) {

        boolean deleted =
                service.deleteUser(id);

        if (!deleted) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .noContent()
                .build();
    }
}