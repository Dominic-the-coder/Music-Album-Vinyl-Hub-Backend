package com.example.mini_project.controllers;

import com.example.mini_project.models.SongCategory;
import com.example.mini_project.services.SongCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SongCategoryController {

    @Autowired
    private SongCategoryService service;

    // GET /song-categories
    @GetMapping("/song-categories")
    public ResponseEntity<List<SongCategory>> getAllCategories() {

        return ResponseEntity.ok(
                service.getAllCategories()
        );
    }

    // GET /song-categories/{id}
    @GetMapping("/song-categories/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable int id) {

        SongCategory category =
                service.getCategoryById(id);

        if (category == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Song category not found");
        }

        return ResponseEntity.ok(category);
    }

    // POST /song-categories
    @PostMapping("/song-categories")
    public ResponseEntity<SongCategory> addCategory(
            @RequestBody SongCategory category) {

        service.saveCategory(category);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(category);
    }

    // PUT /song-categories/{id}
    @PutMapping("/song-categories/songs/{id}")
    public ResponseEntity<?> updatedCategory(
            @PathVariable int id,
            @RequestBody SongCategory updatedCategory) {

        SongCategory category = service.updateCategory(id, updatedCategory);

        if (category == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Song category not found");
        }

        return ResponseEntity.ok(
                service.getCategoryById(id)
        );
    }

    // DELETE /song-categories/{id}
    @DeleteMapping("/song-categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {

        boolean deleted =
                service.deleteCategory(id);

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Song category not found");
        }

        return ResponseEntity.ok(
                "Song category deleted successfully"
        );
    }
}