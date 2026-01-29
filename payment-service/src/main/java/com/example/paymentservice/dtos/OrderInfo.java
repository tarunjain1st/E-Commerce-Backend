package com.example.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderInfo {
    private Long orderId;
    private String customerEmail;
    private List<CheckoutItem> items;
    public Double getTotalAmount() {
        if (items == null) return 0d;
        return items.stream()
                .mapToDouble(item -> item.getUnitAmount() * item.getQuantity())
                .sum();
    }
}
