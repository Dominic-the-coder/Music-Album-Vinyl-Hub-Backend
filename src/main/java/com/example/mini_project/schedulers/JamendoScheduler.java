package com.example.mini_project.schedulers;

import com.example.mini_project.services.AlbumService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JamendoScheduler {

    private final AlbumService albumService;

    public JamendoScheduler(
            AlbumService albumService
    ) {
        this.albumService = albumService;
    }


    @Scheduled(fixedRate = 60000)
    public void syncAlbums() {

        System.out.println("=================================");
        System.out.println("JAMENDO SCHEDULER RUNNING");
        System.out.println("=================================");

        try {

            albumService.importJamendoAlbums();

        } catch (Exception e) {

            System.out.println("=================================");
            System.out.println("JAMENDO SCHEDULER ERROR");
            System.out.println("=================================");

            e.printStackTrace();
        }
    }
}