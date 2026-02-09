package com.example.cartservice.exceptions;

public class InvalidProductIdException extends RuntimeException {
    public InvalidProductIdException(Long productId) {
        super("Invalid product id: " + productId);
    }
}
