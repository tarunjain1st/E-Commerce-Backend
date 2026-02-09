package com.example.paymentservice.exceptions;

public class PaymentGatewayUnavailableException extends RuntimeException {
    public PaymentGatewayUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}