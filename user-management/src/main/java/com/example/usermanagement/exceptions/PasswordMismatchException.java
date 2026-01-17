package com.example.usermanagement.exceptions;

public class PasswordMismatchException extends RuntimeException{
    public PasswordMismatchException(String message) {
        super(message);
    }
}
