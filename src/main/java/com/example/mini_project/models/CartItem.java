package com.example.mini_project.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "cart_id", nullable = false)
    private int cartId;

    @Column(name = "album_id", nullable = false)
    private int albumId;

    @Transient
    private Album album;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price;
}