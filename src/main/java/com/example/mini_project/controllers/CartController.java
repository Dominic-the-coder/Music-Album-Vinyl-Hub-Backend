package com.example.mini_project.controllers;

import com.example.mini_project.models.Cart;
import com.example.mini_project.models.CartItem;
import com.example.mini_project.models.UpdateCartItemRequest;
import com.example.mini_project.services.CartService;
import com.example.mini_project.models.AddToCartRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService service;


    // Get all carts
    @GetMapping("/carts")
    public List<Cart> getAllCarts() {

        return service.getAllCarts();
    }


    // Get cart by ID
    @GetMapping("/carts/{id}")
    public ResponseEntity<Cart> getCartById(
            @PathVariable int id
    ) {

        Cart cart =
                service.getCartById(id);

        if (cart == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(cart);
    }


    // Get cart by user ID
    @GetMapping("/carts/user/{userId}")
    public ResponseEntity<Cart> getCartByUserId(
            @PathVariable int userId
    ) {

        Cart cart =
                service.getCartByUserId(userId);

        if (cart == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(cart);
    }


    // Add album to cart
    @PostMapping("/carts")
    public ResponseEntity<?> addToCart(
            @RequestBody AddToCartRequest request
    ) {

        Cart cart =
                service.addToCart(
                        request.getUserId(),
                        request.getAlbumId(),
                        request.getQuantity()
                );

        if (cart == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(cart);
    }


    // Update cart item quantity
    @PutMapping("/carts/item/{itemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable int itemId,
            @RequestBody UpdateCartItemRequest request
    ) {

        try {

            CartItem updatedItem =
                    service.updateCartItem(
                            itemId,
                            request.getQuantity()
                    );

            return ResponseEntity.ok(updatedItem);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // Delete one cart item
    @DeleteMapping("/carts/item/{itemId}")
    public ResponseEntity<?> deleteCartItem(
            @PathVariable int itemId
    ) {

        if (service.deleteCartItem(itemId)) {

            return ResponseEntity.ok(
                    "Cart item deleted successfully"
            );
        }

        return ResponseEntity
                .notFound()
                .build();
    }
}