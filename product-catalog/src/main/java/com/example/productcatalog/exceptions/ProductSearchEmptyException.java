package com.example.productcatalog.exceptions;

public class ProductSearchEmptyException extends RuntimeException {
    public ProductSearchEmptyException(String message) {
        super(message);
    }
}