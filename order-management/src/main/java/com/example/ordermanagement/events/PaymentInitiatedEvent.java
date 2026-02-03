package com.example.ordermanagement.events;

import lombok.Data;

@Data
public class PaymentInitiatedEvent {
    private Long orderId;
}
