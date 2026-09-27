package com.example.mini_project.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AlbumResponse {

    private Integer id;
    private String jamendoId;
    private String title;
    private String artist;
    private String imageUrl;
    private LocalDate releaseDate;
    private Double price;
    private String genre;
    private List<Song> songs;
}