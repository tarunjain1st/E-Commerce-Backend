package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CartRequestDto {
    private String cartId;
    private Long userId;
    private List<CartItemRequestDto> items;
}
