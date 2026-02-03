package com.example.ordermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CreateOrderRequestDto {
    private Long userId;
    private String customerEmail;
    private String customerName;
    private OrderAddressDto deliveryAddress;
}

