package com.example.mini_project.repositories;

import com.example.mini_project.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {

    Optional<Album> findByJamendoId(String jamendoId);

    boolean existsByJamendoId(String jamendoId);
}