package com.example.notificationservice.events;

import lombok.Data;

@Data
public class OrderPlacedEvent {
    private String orderId;
    private String email;
    private String name;
    private Double totalAmount;
}
