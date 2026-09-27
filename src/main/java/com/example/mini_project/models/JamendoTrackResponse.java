package com.example.mini_project.models;

import lombok.Data;

import java.util.List;

@Data
public class JamendoTrackResponse {

    private List<JamendoTrack> results;
}