package com.example.mini_project.services;

import com.example.mini_project.models.*;
import com.example.mini_project.repositories.AlbumRepository;
import com.example.mini_project.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private JamendoAlbumService jamendoAlbumService;

    @Autowired
    private JamendoTrackService jamendoTrackService;


    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }


    public List<AlbumResponse> getAllAlbumsWithSongs() {

        List<Album> albums = albumRepository.findAll();

        return albums.stream()
                .map(album -> {

                    AlbumResponse response = new AlbumResponse();

                    response.setId(album.getId());
                    response.setJamendoId(album.getJamendoId());
                    response.setTitle(album.getTitle());
                    response.setArtist(album.getArtist());
                    response.setImageUrl(album.getImageUrl());
                    response.setReleaseDate(album.getReleaseDate());
                    response.setPrice(album.getPrice());
                    response.setGenre(album.getGenre());

                    response.setSongs(
                            songRepository.findByAlbumId(album.getId())
                    );

                    return response;
                })
                .toList();
    }


    public boolean updateAlbumPrice(int id, double price) {

        Album album = albumRepository.findById(id).orElse(null);

        if (album == null) {
            return false;
        }

        album.setPrice(price);

        albumRepository.save(album);

        return true;
    }


    public void importJamendoAlbums() {

        System.out.println("=================================");
        System.out.println("STARTING JAMENDO IMPORT");
        System.out.println("=================================");

        List<JamendoAlbum> jamendoAlbums =
                jamendoAlbumService.getAlbums();

        System.out.println("Albums received: " + jamendoAlbums.size());

        for (int i = 0; i < jamendoAlbums.size(); i++) {

            JamendoAlbum jamendoAlbum = jamendoAlbums.get(i);

            System.out.println(
                    "Importing album: " + jamendoAlbum.getName()
            );

            Album album = albumRepository
                    .findByJamendoId(jamendoAlbum.getId())
                    .orElseGet(Album::new);

            album.setJamendoId(jamendoAlbum.getId());
            album.setTitle(jamendoAlbum.getName());
            album.setArtist(jamendoAlbum.getArtistName());
            album.setImageUrl(jamendoAlbum.getImage());

            // Assign exactly 3 albums to each genre
            album.setGenre(getGenreForAlbum(i));

            System.out.println(
                    "Genre: " + album.getGenre()
            );


            // Convert Jamendo String date to LocalDate
            if (jamendoAlbum.getReleaseDate() != null &&
                    !jamendoAlbum.getReleaseDate().isEmpty()) {

                album.setReleaseDate(
                        LocalDate.parse(
                                jamendoAlbum.getReleaseDate().substring(0, 10)
                        )
                );
            }


            if (album.getPrice() == null) {
                album.setPrice(59.90);
            }


            if (album.getQuantity() == null) {
                album.setQuantity(0);
            }


            album = albumRepository.save(album);


            // Get songs for this album
            List<JamendoTrack> tracks =
                    jamendoTrackService.getTracksByAlbumId(
                            jamendoAlbum.getId()
                    );

            System.out.println(
                    "Tracks received: " + tracks.size()
            );


            for (JamendoTrack track : tracks) {

                Song song = songRepository
                        .findByJamendoId(track.getId())
                        .orElseGet(Song::new);

                song.setJamendoId(track.getId());
                song.setTitle(track.getName());


                if (track.getArtistName() != null &&
                        !track.getArtistName().isEmpty()) {

                    song.setArtist(track.getArtistName());

                } else {

                    song.setArtist(jamendoAlbum.getArtistName());
                }


                song.setAudioUrl(track.getAudio());


                if (track.getImage() != null &&
                        !track.getImage().isEmpty()) {

                    song.setImageUrl(track.getImage());

                } else {

                    song.setImageUrl(jamendoAlbum.getImage());
                }


                song.setDuration(track.getDuration());

                song.setAlbumId(album.getId());

                songRepository.save(song);
            }
        }

        System.out.println("=================================");
        System.out.println("JAMENDO IMPORT FINISHED");
        System.out.println("=================================");
    }


    // Assign 3 albums to each genre
    private String getGenreForAlbum(int index) {

        String[] genres = {
                "Pop",
                "Pop",
                "Pop",

                "Rock",
                "Rock",
                "Rock",

                "Indie",
                "Indie",
                "Indie",

                "Hip Hop",
                "Hip Hop",
                "Hip Hop",

                "Jazz",
                "Jazz",
                "Jazz"
        };

        return genres[index % genres.length];
    }
}