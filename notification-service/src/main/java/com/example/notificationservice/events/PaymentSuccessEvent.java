package com.example.notificationservice.events;

import lombok.Data;

@Data
public class PaymentSuccessEvent {
    private String paymentId;
    private String email;
    private String name;
    private Double amount;
}
