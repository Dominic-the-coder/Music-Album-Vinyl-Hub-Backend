package com.example.mini_project.controllers;

import com.example.mini_project.models.JamendoTrack;
import com.example.mini_project.models.Song;
import com.example.mini_project.services.JamendoTrackService;
import com.example.mini_project.services.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private JamendoTrackService jamendoTrackService;


    // Search songs saved in your backend
    @GetMapping("/search/songs")
    public List<Song> searchSongs(
            @RequestParam String keyword
    ) {

        return searchService.searchSongs(keyword);
    }


    // Search songs from Jamendo
    @GetMapping("/search/jamendo")
    public List<JamendoTrack> searchJamendo(
            @RequestParam String keyword
    ) {

        return jamendoTrackService.searchTracks(keyword);
    }
}