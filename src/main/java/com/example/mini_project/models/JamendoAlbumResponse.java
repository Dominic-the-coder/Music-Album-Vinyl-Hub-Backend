package com.example.mini_project.models;

import lombok.Data;

import java.util.List;

@Data
public class JamendoAlbumResponse {

    private List<JamendoAlbum> results;
}