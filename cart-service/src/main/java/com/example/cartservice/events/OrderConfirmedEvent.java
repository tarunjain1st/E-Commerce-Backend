package com.example.cartservice.events;

import lombok.Data;

@Data
public class OrderConfirmedEvent {
    private Long userId;
    private Long orderId;
}
