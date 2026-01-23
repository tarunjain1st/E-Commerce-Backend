package com.example.payment.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    Long amount;
    String orderId;
    String name;
    String email;
    String phoneNumber;
    String description;

}
