package com.example.ordermanagement.events;

import lombok.Data;

@Data
public class PaymentSuccessEvent {
    private Long orderId;
    private String paymentId;
    private String email;
    private String name;
    private Double amount;
}
