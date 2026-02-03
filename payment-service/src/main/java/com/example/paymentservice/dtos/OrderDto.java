package com.example.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long orderId;
    private String customerEmail;
    private String customerName;
    private List<OrderItemDto> items;
    public Double getTotalAmount() {
        if (items == null) return 0d;
        return items.stream()
                .mapToDouble(item -> item.getUnitAmount() * item.getQuantity())
                .sum();
    }
}
