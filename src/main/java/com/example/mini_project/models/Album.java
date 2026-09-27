package com.example.mini_project.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "albums")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String jamendoId;

    private String title;

    private String artist;

    private String imageUrl;

    private LocalDate releaseDate;

    private Double price;

    private Integer quantity = 0;

    private String genre;
}