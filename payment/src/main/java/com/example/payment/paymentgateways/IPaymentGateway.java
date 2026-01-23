package com.example.payment.paymentgateways;

public interface IPaymentGateway {
    String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description);
}
