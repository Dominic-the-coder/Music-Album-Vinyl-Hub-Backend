package com.example.mini_project.services;

import com.example.mini_project.models.SongCategory;
import com.example.mini_project.repositories.SongCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongCategoryService {

    @Autowired
    private SongCategoryRepository repository;


    // Get all categories
    public List<SongCategory> getAllCategories() {
        return repository.findAll();
    }


    // Get category by ID
    public SongCategory getCategoryById(int id) {
        return repository.findById(id).orElse(null);
    }


    // Save category
    public SongCategory saveCategory(SongCategory category) {
        return repository.save(category);
    }


    // Update category
    public SongCategory updateCategory(
            int id,
            SongCategory updatedCategory
    ) {

        SongCategory category =
                repository.findById(id).orElse(null);

        if (category == null) {
            return null;
        }

        category.setName(updatedCategory.getName());

        return repository.save(category);
    }


    // Delete category
    public boolean deleteCategory(int id) {

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);

        return true;
    }
}