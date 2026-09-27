package com.example.mini_project.models;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}