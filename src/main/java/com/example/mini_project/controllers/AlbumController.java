package com.example.mini_project.controllers;

import com.example.mini_project.models.AlbumResponse;
import com.example.mini_project.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AlbumController {

    @Autowired
    private AlbumService albumService;


    @GetMapping("/albums/with-songs")
    public List<AlbumResponse> getAllAlbumsWithSongs() {

        return albumService.getAllAlbumsWithSongs();
    }


    @PostMapping("/albums/import-jamendo")
    public String importJamendoAlbums() {

        albumService.importJamendoAlbums();

        return "Jamendo albums imported successfully";
    }


    @PutMapping("/albums/{id}/price")
    public String updateAlbumPrice(
            @PathVariable int id,
            @RequestParam double price
    ) {

        boolean updated =
                albumService.updateAlbumPrice(id, price);

        if (!updated) {
            return "Album not found";
        }

        return "Album price updated successfully";
    }
}