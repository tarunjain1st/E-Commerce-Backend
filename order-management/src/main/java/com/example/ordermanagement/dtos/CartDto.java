package com.example.ordermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartDto {
    private String cartId;
    private Long userId;
    private List<CartItemDto> items;
    private Double totalPrice;
}
