package com.example.ordermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemResponseDto {
    private Long productId;
    private String productName;
    private int quantity;
    private Double unitPrice;
}
