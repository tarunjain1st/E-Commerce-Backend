package com.example.ordermanagement.dtos;

import com.example.ordermanagement.models.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private OrderStatus status;
    private Double totalAmount;
    private List<OrderItemResponseDto> items;
    private OrderAddressDto deliveryAddress;
}
