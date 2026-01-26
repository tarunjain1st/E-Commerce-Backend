package com.example.cartservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItemData {
    private Long productId;
    private Integer quantity;
}
