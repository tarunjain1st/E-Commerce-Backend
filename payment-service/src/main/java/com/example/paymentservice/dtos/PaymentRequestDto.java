package com.example.paymentservice.dtos;

import com.example.paymentservice.models.PaymentGateway;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private Long orderId;
    private PaymentGateway paymentGateway;
}
