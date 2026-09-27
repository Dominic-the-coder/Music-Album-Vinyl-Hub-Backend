package com.example.mini_project.models;

import lombok.Data;

@Data
public class AddToCartRequest {

    private int userId;
    private int albumId;
    private int quantity;
}