package com.example.payment.services;

import com.example.payment.dtos.SessionDto;

import java.util.List;

public interface IPaymentService {
    String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description);
    SessionDto createSession(String successUrl, List<Long> amounts, List<String> productNames, List<Long> quantities);
}
