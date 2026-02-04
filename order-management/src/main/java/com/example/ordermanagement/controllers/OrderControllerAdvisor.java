package com.example.ordermanagement.controllers;

import com.example.ordermanagement.exceptions.EmptyCartException;
import com.example.ordermanagement.exceptions.InvalidOrderRequestException;
import com.example.ordermanagement.exceptions.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderControllerAdvisor {

    // 400 - Bad Request
    @ExceptionHandler(InvalidOrderRequestException.class)
    public ResponseEntity<String> handleBadRequest(InvalidOrderRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // 404 - Not Found
    @ExceptionHandler({OrderNotFoundException.class, EmptyCartException.class})
    public ResponseEntity<String> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
}
