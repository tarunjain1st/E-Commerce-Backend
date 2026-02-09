package com.example.paymentservice.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {
    private String sessionId;
    private String checkoutUrl;
}
