package com.example.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutItem {
    private String productName;
    private Long unitAmount;
    private Long quantity;
}
