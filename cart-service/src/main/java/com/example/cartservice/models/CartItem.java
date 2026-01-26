package com.example.cartservice.models;

import lombok.Data;

@Data
public class CartItem {
    private Long productId;
    private String productName; // fetched from product service
    private Integer quantity;
}