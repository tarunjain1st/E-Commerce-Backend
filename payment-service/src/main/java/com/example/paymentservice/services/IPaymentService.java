package com.example.paymentservice.services;

import com.example.paymentservice.dtos.PaymentResponse;
import com.example.paymentservice.models.PaymentGateway;

public interface IPaymentService {
    PaymentResponse createPayment(Long orderId, PaymentGateway gateway);
    Boolean verifyPaymentStatus(String sessionId, PaymentGateway gateway);
}
