package com.example.mini_project.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JamendoTrack {

    private String id;

    private String name;

    private int duration;

    @JsonProperty("artist_name")
    private String artistName;

    @JsonProperty("album_id")
    private String albumId;

    @JsonProperty("album_name")
    private String albumName;

    private String audio;

    private String image;
}