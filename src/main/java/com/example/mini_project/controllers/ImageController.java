package com.example.mini_project.controllers;

import com.example.mini_project.models.Image;
import com.example.mini_project.models.ImageResponse;
import com.example.mini_project.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController

public class ImageController {

    @Autowired
    private ImageService service;

    // Upload image
    @PostMapping(
            value = "/images/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ImageResponse> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Image image = service.uploadImage(file);

            ImageResponse response = new ImageResponse(
                    image.getId(),
                    image.getName(),
                    image.getType()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().build();
        }
    }

    // Get image
    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable Integer id
    ) {

        Image image = service.findById(id);

        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        System.out.println("IMAGE ID: " + image.getId());
        System.out.println("IMAGE NAME: " + image.getName());
        System.out.println("IMAGE TYPE: " + image.getType());
        System.out.println("IMAGE SIZE: " + image.getData().length);

        MediaType mediaType;

        try {
            mediaType = MediaType.parseMediaType(image.getType());
        } catch (Exception e) {
            mediaType = MediaType.IMAGE_JPEG;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(image.getData().length)
                .body(image.getData());
    }
}