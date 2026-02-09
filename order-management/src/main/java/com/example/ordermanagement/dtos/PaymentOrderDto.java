package com.example.ordermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaymentOrderDto {
    private Long orderId;
    private String customerEmail;
    private Double totalAmount;
    private String customerName;
    private List<PaymentOrderItemDto> items;
}
