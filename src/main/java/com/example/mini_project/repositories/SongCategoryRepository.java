package com.example.mini_project.repositories;

import com.example.mini_project.models.SongCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongCategoryRepository extends JpaRepository<SongCategory, Integer> {

}