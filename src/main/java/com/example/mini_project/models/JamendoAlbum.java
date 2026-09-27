package com.example.mini_project.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JamendoAlbum {

    private String id;

    private String name;

    @JsonProperty("artist_id")
    private String artistId;

    @JsonProperty("artist_name")
    private String artistName;

    private String image;

    @JsonProperty("releasedate")
    private String releaseDate;

    private JamendoMusicInfo musicinfo;
}