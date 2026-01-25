package com.example.ordermanagement.services;


import com.example.ordermanagement.models.Order;

import java.util.UUID;

public interface IOrderService {
    Order createOrder(Long customerId, Double totalAmount);

    Boolean deleteOrder(UUID orderId);
}