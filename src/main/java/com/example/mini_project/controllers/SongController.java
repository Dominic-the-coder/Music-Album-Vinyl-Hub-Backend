package com.example.mini_project.controllers;

import com.example.mini_project.models.JamendoTrack;
import com.example.mini_project.models.Song;
import com.example.mini_project.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private SongService service;


    // GET /songs
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {

        return ResponseEntity.ok(
                service.getAllSongs()
        );
    }


    // GET /songs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getSongById(
            @PathVariable int id
    ) {

        Song song = service.getSongById(id);

        if (song == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Song not found");
        }

        return ResponseEntity.ok(song);
    }


    // GET /songs/album/{albumId}
    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Song>> getSongsByAlbum(
            @PathVariable int albumId
    ) {

        return ResponseEntity.ok(
                service.getSongsByAlbum(albumId)
        );
    }


    // GET /songs/search?keyword=love
    @GetMapping("/search")
    public ResponseEntity<List<JamendoTrack>> searchJamendoSongs(
            @RequestParam String keyword
    ) {

        return ResponseEntity.ok(
                service.searchJamendoSongs(keyword)
        );
    }


    // GET /songs/jamendo/{jamendoId}
    @GetMapping("/jamendo/{jamendoId}")
    public ResponseEntity<?> getJamendoSongById(
            @PathVariable String jamendoId
    ) {

        JamendoTrack track =
                service.getJamendoSongById(jamendoId);

        if (track == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Jamendo song not found");
        }

        return ResponseEntity.ok(track);
    }


    // POST /songs
    @PostMapping
    public ResponseEntity<Song> addSong(
            @RequestBody Song song
    ) {

        Song savedSong = service.saveSong(song);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedSong);
    }


    // PUT /songs/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSong(
            @PathVariable int id,
            @RequestBody Song updatedSong
    ) {

        Song song =
                service.updateSong(
                        id,
                        updatedSong
                );

        if (song == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Song not found");
        }

        return ResponseEntity.ok(song);
    }


    // DELETE /songs/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(
            @PathVariable int id
    ) {

        boolean deleted =
                service.deleteSong(id);

        if (!deleted) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Song not found");
        }

        return ResponseEntity.ok(
                "Song deleted successfully"
        );
    }
}