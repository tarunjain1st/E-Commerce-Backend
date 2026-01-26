package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponseDto {
    private String cartId;
    private Long userId;
    private List<CartItemResponseDto> items;
}
