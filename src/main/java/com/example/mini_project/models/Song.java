package com.example.mini_project.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String jamendoId;

    private String title;

    private String artist;

    private String audioUrl;

    private String imageUrl;

    private Integer duration;

    private Integer albumId;

    private String genre;
}