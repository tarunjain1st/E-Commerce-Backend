package com.example.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentResponseDto {
    private String sessionId;       // Stripe session ID
    private String checkoutUrl;     // URL to redirect user

}
