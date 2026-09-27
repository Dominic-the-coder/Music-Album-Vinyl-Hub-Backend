package com.example.mini_project.repositories;

import com.example.mini_project.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    CartItem findByCartIdAndAlbumId(int cartId, int albumId);

    List<CartItem> findByCartId(int cartId);
}