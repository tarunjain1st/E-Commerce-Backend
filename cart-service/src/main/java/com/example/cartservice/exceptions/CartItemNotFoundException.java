package com.example.cartservice.exceptions;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(Long productId) {
        super("Product not found in cart: " + productId);
    }
}
