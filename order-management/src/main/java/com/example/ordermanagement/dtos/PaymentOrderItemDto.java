package com.example.ordermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentOrderItemDto {
    private String productName;
    private Long unitAmount;
    private Long quantity;
}
