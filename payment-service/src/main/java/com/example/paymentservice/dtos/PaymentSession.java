package com.example.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PaymentSession {
    private String sessionId;
    private String checkoutUrl;
    private Long amount;
    private String status;
    private String customerEmail;
    private Date expiresAt;
}
