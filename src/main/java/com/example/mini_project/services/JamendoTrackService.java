package com.example.mini_project.services;

import com.example.mini_project.models.JamendoTrack;
import com.example.mini_project.models.JamendoTrackResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class JamendoTrackService {

    private final RestClient restClient;
    private final String clientId;

    public JamendoTrackService(
            @Value("${jamendo.base-url}") String baseUrl,
            @Value("${jamendo.client-id}") String clientId
    ) {
        this.restClient = RestClient.create(baseUrl);
        this.clientId = clientId;
    }


    // Get songs for an album
    public List<JamendoTrack> getTracksByAlbumId(String albumId) {

        String url = UriComponentsBuilder
                .fromUriString("/tracks/")
                .queryParam("client_id", clientId)
                .queryParam("format", "json")
                .queryParam("album_id", albumId)
                .queryParam("audioformat", "mp32")
                .queryParam("limit", 50)
                .build()
                .toUriString();

        System.out.println("=================================");
        System.out.println("JAMENDO TRACK URL:");
        System.out.println(url);
        System.out.println("=================================");

        JamendoTrackResponse response = restClient
                .get()
                .uri(url)
                .retrieve()
                .body(JamendoTrackResponse.class);

        if (response == null ||
                response.getResults() == null) {

            return List.of();
        }

        return response.getResults();
    }


    // Search songs from Jamendo
    public List<JamendoTrack> searchTracks(String keyword) {

        String url = UriComponentsBuilder
                .fromUriString("/tracks/")
                .queryParam("client_id", clientId)
                .queryParam("format", "json")
                .queryParam("namesearch", keyword)
                .queryParam("audioformat", "mp32")
                .queryParam("limit", 20)
                .build()
                .toUriString();

        System.out.println("=================================");
        System.out.println("JAMENDO SEARCH URL:");
        System.out.println(url);
        System.out.println("=================================");

        JamendoTrackResponse response = restClient
                .get()
                .uri(url)
                .retrieve()
                .body(JamendoTrackResponse.class);

        if (response == null ||
                response.getResults() == null) {

            return List.of();
        }

        return response.getResults();
    }


    // Get one song from Jamendo
    public JamendoTrack getTrackById(String jamendoId) {

        String url = UriComponentsBuilder
                .fromUriString("/tracks/")
                .queryParam("client_id", clientId)
                .queryParam("format", "json")
                .queryParam("id", jamendoId)
                .queryParam("audioformat", "mp32")
                .build()
                .toUriString();

        System.out.println("=================================");
        System.out.println("JAMENDO SONG URL:");
        System.out.println(url);
        System.out.println("=================================");

        JamendoTrackResponse response = restClient
                .get()
                .uri(url)
                .retrieve()
                .body(JamendoTrackResponse.class);

        if (response == null ||
                response.getResults() == null ||
                response.getResults().isEmpty()) {

            return null;
        }

        return response.getResults().get(0);
    }
}