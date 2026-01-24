package com.example.payment.paymentgateways;

import com.example.payment.dtos.SessionDto;

import java.util.List;

public interface IPaymentGateway {
    String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description);
    SessionDto createSession(String successUrl, List<Long> amounts, List<String> productNames, List<Long> quantities);
}
