package com.example.usermanagement.exceptions;

public class UserAlreadySignedUpException extends RuntimeException{
    public UserAlreadySignedUpException(String message){
        super(message);
    }
}
