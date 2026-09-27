package com.example.mini_project.services;

import com.example.mini_project.models.Image;
import com.example.mini_project.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository repository;

    public Image save(Image image) {
        return repository.save(image);
    }

    public Image uploadImage(MultipartFile file) throws Exception {

        Image image = new Image();

        image.setName(file.getOriginalFilename());
        image.setType(file.getContentType());
        image.setData(file.getBytes());

        return repository.save(image);
    }

    public Image findById(int id) {
        return repository.findById(id).orElse(null);
    }

    public List<Image> findAll() {
        return repository.findAll();
    }
}