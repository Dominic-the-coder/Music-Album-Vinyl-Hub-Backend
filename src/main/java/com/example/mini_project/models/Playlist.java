package com.example.mini_project.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "jamendo_id", nullable = false, unique = true)
    private String jamendoId;

    @Column(name = "name")
    private String name;

    @Column(name = "creation_date")
    private String creationDate;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "zip_url", length = 1000)
    private String zipUrl;

    @Column(name = "short_url", length = 1000)
    private String shortUrl;

    @Column(name = "share_url", length = 1000)
    private String shareUrl;
}