package com.example.mini_project.services;

import com.example.mini_project.models.JamendoTrack;
import com.example.mini_project.models.Song;
import com.example.mini_project.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {

    @Autowired
    private SongRepository repository;

    @Autowired
    private JamendoTrackService jamendoTrackService;


    // Get all songs
    public List<Song> getAllSongs() {
        return repository.findAll();
    }


    // Get song by ID
    public Song getSongById(int id) {
        return repository.findById(id).orElse(null);
    }


    // Get songs by album
    public List<Song> getSongsByAlbum(int albumId) {
        return repository.findByAlbumId(albumId);
    }


    // Save song
    public Song saveSong(Song song) {
        return repository.save(song);
    }


    // Search songs from Jamendo
    public List<JamendoTrack> searchJamendoSongs(
            String keyword
    ) {
        return jamendoTrackService.searchTracks(keyword);
    }


    // Get one song from Jamendo
    public JamendoTrack getJamendoSongById(
            String jamendoId
    ) {
        return jamendoTrackService.getTrackById(jamendoId);
    }


    // Update song
    public Song updateSong(
            int id,
            Song updatedSong
    ) {

        Song song =
                repository.findById(id).orElse(null);

        if (song == null) {
            return null;
        }

        song.setJamendoId(
                updatedSong.getJamendoId()
        );

        song.setTitle(
                updatedSong.getTitle()
        );

        song.setArtist(
                updatedSong.getArtist()
        );

        song.setAudioUrl(
                updatedSong.getAudioUrl()
        );

        song.setImageUrl(
                updatedSong.getImageUrl()
        );

        song.setDuration(
                updatedSong.getDuration()
        );

        // Update album connection
        song.setAlbumId(
                updatedSong.getAlbumId()
        );

        return repository.save(song);
    }


    // Delete song
    public boolean deleteSong(int id) {

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);

        return true;
    }
}