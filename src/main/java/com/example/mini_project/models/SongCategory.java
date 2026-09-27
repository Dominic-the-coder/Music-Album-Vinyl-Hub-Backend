package com.example.mini_project.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "song_categories")
public class SongCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;
}