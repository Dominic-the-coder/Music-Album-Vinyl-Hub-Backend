package com.example.mini_project.services;

import com.example.mini_project.models.JamendoAlbum;
import com.example.mini_project.models.JamendoAlbumResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class JamendoAlbumService {

    @Value("${jamendo.base-url}")
    private String baseUrl;

    @Value("${jamendo.client-id}")
    private String clientId;

    private final RestClient restClient = RestClient.create();

    public List<JamendoAlbum> getAlbums() {

        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/albums/musicinfo/")
                .queryParam("client_id", clientId)
                .queryParam("format", "json")
                .queryParam("limit", 15)
                .build()
                .toUriString();

        System.out.println("=================================");
        System.out.println("JAMENDO ALBUM MUSIC INFO URL:");
        System.out.println(url);
        System.out.println("=================================");

        JamendoAlbumResponse response = restClient
                .get()
                .uri(url)
                .retrieve()
                .body(JamendoAlbumResponse.class);

        if (response == null || response.getResults() == null) {
            return List.of();
        }

        return response.getResults();
    }
}