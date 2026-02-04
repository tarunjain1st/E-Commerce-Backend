package com.example.ordermanagement.exceptions;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException(Long userId) {
        super("Cart is empty for user: " + userId);
    }
}
