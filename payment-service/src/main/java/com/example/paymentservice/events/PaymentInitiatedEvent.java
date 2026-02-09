package com.example.paymentservice.events;

import lombok.Data;

@Data
public class PaymentInitiatedEvent {
    private Long orderId;
}
