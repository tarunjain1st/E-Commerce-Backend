package com.example.cartservice.services;

import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItemData;

import java.util.List;

public interface ICartService {

    Cart getCartByUserId(Long userId);

    Cart addItemToCart(Long userId, CartItemData item);

    Cart updateItemInCart(Long userId, CartItemData item);

    Cart removeItemFromCart(Long userId, Long productId);

    void clearCart(Long userId);
}
