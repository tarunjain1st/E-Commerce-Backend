package com.example.paymentservice.dtos;


import com.example.paymentservice.models.PaymentStatus;
import lombok.Data;

@Data
public class PaymentWebhookResponse {
    private String paymentReference;
    private PaymentStatus status;
    private String failureReason; // card_declined, insufficient_funds, timeout, etc
}