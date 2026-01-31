package com.example.cartservice.services;

import com.example.cartservice.models.Cart;

public interface ICartService {

    Cart getCartByUserId(Long userId);
    Cart addItemToCart(Long userId, Long productId, Integer quantity);
    Cart removeItemFromCart(Long userId, Long productId);
    void clearCart(Long userId);
    Cart checkoutCart(Long userId);
}
