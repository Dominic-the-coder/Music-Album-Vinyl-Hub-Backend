package com.example.mini_project.services;

import com.example.mini_project.models.Album;
import com.example.mini_project.models.Cart;
import com.example.mini_project.models.CartItem;
import com.example.mini_project.models.User;
import com.example.mini_project.repositories.AlbumRepository;
import com.example.mini_project.repositories.CartItemRepository;
import com.example.mini_project.repositories.CartRepository;
import com.example.mini_project.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;


    // Get all carts
    public List<Cart> getAllCarts() {

        List<Cart> carts =
                cartRepository.findAll();

        for (Cart cart : carts) {
            loadCartDetails(cart);
        }

        return carts;
    }


    // Get cart by ID
    public Cart getCartById(int id) {

        Cart cart =
                cartRepository
                        .findById(id)
                        .orElse(null);

        if (cart == null) {
            return null;
        }

        loadCartDetails(cart);

        return cart;
    }


    // Get cart by user ID
    public Cart getCartByUserId(int userId) {

        Cart cart =
                cartRepository
                        .findByUserId(userId)
                        .orElse(null);

        if (cart == null) {
            return null;
        }

        loadCartDetails(cart);

        return cart;
    }


    // Load user, items and album information
    private void loadCartDetails(Cart cart) {

        User user =
                userRepository
                        .findById(cart.getUserId())
                        .orElse(null);

        cart.setUser(user);

        List<CartItem> items =
                cartItemRepository
                        .findByCartId(cart.getId());

        for (CartItem item : items) {

            Album album =
                    albumRepository
                            .findById(item.getAlbumId())
                            .orElse(null);

            item.setAlbum(album);
        }

        cart.setItems(items);
    }


    // Add album to cart
    public Cart addToCart(
            int userId,
            int albumId,
            int quantity
    ) {

        Album album =
                albumRepository
                        .findById(albumId)
                        .orElse(null);

        if (album == null) {
            return null;
        }


        User user =
                userRepository
                        .findById(userId)
                        .orElse(null);

        if (user == null) {
            return null;
        }


        Cart cart =
                cartRepository
                        .findByUserId(userId)
                        .orElse(null);


        if (cart == null) {

            cart = new Cart();

            cart.setUserId(userId);

            cart =
                    cartRepository.save(cart);
        }


        CartItem existingItem =
                cartItemRepository
                        .findByCartIdAndAlbumId(
                                cart.getId(),
                                albumId
                        );


        if (existingItem != null) {

            // Increase quantity when album already exists
            existingItem.setQuantity(
                    existingItem.getQuantity() + quantity
            );

            existingItem.setPrice(
                    album.getPrice()
            );

            cartItemRepository.save(
                    existingItem
            );

        } else {

            CartItem item =
                    new CartItem();

            item.setCartId(
                    cart.getId()
            );

            item.setAlbumId(
                    albumId
            );

            item.setQuantity(
                    quantity
            );

            item.setPrice(
                    album.getPrice()
            );

            cartItemRepository.save(item);
        }


        loadCartDetails(cart);

        return cart;
    }


    // Update cart item quantity
    public CartItem updateCartItem(
            int itemId,
            int quantity
    ) {

        if (quantity < 1) {
            throw new RuntimeException(
                    "Quantity must be at least 1"
            );
        }

        CartItem cartItem =
                cartItemRepository
                        .findById(itemId)
                        .orElse(null);

        if (cartItem == null) {
            throw new RuntimeException(
                    "Cart item not found"
            );
        }

        cartItem.setQuantity(quantity);

        Album album =
                albumRepository
                        .findById(cartItem.getAlbumId())
                        .orElse(null);

        if (album != null) {
            cartItem.setPrice(
                    album.getPrice()
            );
        }

        return cartItemRepository.save(
                cartItem
        );
    }


    // Delete one cart item
    public boolean deleteCartItem(int itemId) {

        if (!cartItemRepository.existsById(itemId)) {
            return false;
        }

        cartItemRepository.deleteById(itemId);

        return true;
    }
}