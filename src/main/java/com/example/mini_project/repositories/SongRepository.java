package com.example.mini_project.repositories;

import com.example.mini_project.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Integer> {

    // Find song by Jamendo ID
    Optional<Song> findByJamendoId(String jamendoId);

    // Check if Jamendo song already exists
    boolean existsByJamendoId(String jamendoId);

    // Get songs belonging to an album
    List<Song> findByAlbumId(Integer albumId);

    // Search songs by title or artist
    List<Song> findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(
            String title,
            String artist
    );
}