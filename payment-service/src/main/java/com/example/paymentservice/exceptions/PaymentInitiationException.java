package com.example.paymentservice.exceptions;

public class PaymentInitiationException extends RuntimeException {
    public PaymentInitiationException(String message, Throwable cause) {
        super(message, cause);
    }
}