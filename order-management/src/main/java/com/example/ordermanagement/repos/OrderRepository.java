package com.example.ordermanagement.repos;

import com.example.ordermanagement.models.Order;
import com.example.ordermanagement.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Fetch all orders of a user
    List<Order> findByUserId(Long userId);

    // Fetch orders by status
    List<Order> findByStatus(OrderStatus status);

    // Fetch by orderNumber
    Order findByOrderNumber(String orderNumber);
}