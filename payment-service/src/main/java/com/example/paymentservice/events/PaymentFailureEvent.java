package com.example.paymentservice.events;

import lombok.Data;

@Data
public class PaymentFailureEvent {
    private Long orderId;
}
