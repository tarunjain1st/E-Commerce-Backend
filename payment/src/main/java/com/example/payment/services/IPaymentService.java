package com.example.payment.services;

public interface IPaymentService {
    String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description);
}
