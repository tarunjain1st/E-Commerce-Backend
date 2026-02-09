package com.example.ordermanagement.events;

import lombok.Data;

@Data
public class PaymentFailureEvent {
    private Long orderId;
}
