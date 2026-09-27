package com.example.mini_project.services;

import com.example.mini_project.models.Song;
import com.example.mini_project.repositories.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final SongRepository songRepository;

    public SearchService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }


    public List<Song> searchSongs(String keyword) {

        return songRepository
                .findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(
                        keyword,
                        keyword
                );
    }
}