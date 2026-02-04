package com.example.cartservice.exceptions;

public class InvalidCartRequestException extends RuntimeException {
    public InvalidCartRequestException(String message) {
        super(message);
    }
}
