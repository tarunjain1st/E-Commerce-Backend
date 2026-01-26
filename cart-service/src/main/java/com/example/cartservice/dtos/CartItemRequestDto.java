package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemRequestDto {
    private Long productId;
    private Integer quantity;
}
