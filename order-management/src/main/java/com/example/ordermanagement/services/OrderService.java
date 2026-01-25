package com.example.ordermanagement.services;

import com.example.ordermanagement.models.Order;
import com.example.ordermanagement.models.OrderStatus;
import com.example.ordermanagement.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order createOrder(Long customerId, Double totalAmount) {
        //Add your implementation here
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCustomerId(customerId);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(new Date());
        order.setLastUpdatedAt(new Date());
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        return order;
    }

    @Override
    public Boolean deleteOrder(UUID orderId) {
        //Add your implementation here and return result rather than returning false
        return orderRepository.remove(orderId);
    }
}
