package com.example.mini_project.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {

    private int id;
    private String name;
    private String email;
    private String message;
}