package com.example.paymentservice.paymentgateways;

import com.example.paymentservice.dtos.OrderDto;
import com.example.paymentservice.dtos.PaymentResponse;
import com.example.paymentservice.dtos.PaymentSession;

public interface IPaymentGateway {
    String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description);
    //SessionDto createSession(String successUrl, List<Long> amounts, List<String> productNames, List<Long> quantities);
    PaymentResponse createPayment(OrderDto orderDto);
    PaymentSession getPaymentDetails(String sessionId);
}
