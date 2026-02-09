package com.example.notificationservice.events;

import lombok.Data;

@Data
public class OrderPlacedEvent {
    private Long orderId;
    private String email;
    private String name;
    private Double totalAmount;
}
