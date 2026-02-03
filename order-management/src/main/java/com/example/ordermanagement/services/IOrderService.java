package com.example.ordermanagement.services;


import com.example.ordermanagement.models.Order;
import com.example.ordermanagement.models.OrderAddress;
import com.example.ordermanagement.models.OrderStatus;
import com.example.ordermanagement.models.PaymentStatus;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface IOrderService {

    List<Order> getAllOrders();
    List<Order> getOrdersByUserId(Long userId);
    Order getOrderById(Long orderId);
    Order createOrder(Long userId, String customerName, String customerEmail, OrderAddress deliveryAddress);
    Order updateOrderStatus(Long orderId, OrderStatus status);
    void handlePaymentSuccess(Long orderId, String transactionId, Double amount);
    void handlePaymentFailure(Long orderId);
    void handlePaymentInitiated(Long orderId);
}