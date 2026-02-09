package com.example.ordermanagement.events;

import lombok.Data;

@Data
public class OrderPlacedEvent {
    private Long orderId;
    private Long userId;
    private String email;
    private String name;
    private Double totalAmount;
}
