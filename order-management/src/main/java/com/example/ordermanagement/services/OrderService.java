package com.example.ordermanagement.services;

import com.example.ordermanagement.clients.CartClient;
import com.example.ordermanagement.dtos.CartDto;
import com.example.ordermanagement.models.*;
import com.example.ordermanagement.repos.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartClient cartClient;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    @Override
    public Order createOrder(Long userId, String customerEmail, OrderAddress deliveryAddress) {
        // Step 1: Get validated cart snapshot
        CartDto cart = cartClient.getCartByUserId(userId);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty for user " + userId);
        }

        // Step 2: Create Order entity
        Order order = new Order();
        order.setUserId(userId);
        order.setCustomerEmail(customerEmail);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(cart.getTotalPrice());

        // Step 3: Map Cart items → Order items
        List<OrderItem> orderItems = cart.getItems().stream().map(ci -> {
            OrderItem item = new OrderItem();
            item.setProductId(ci.getProductId());
            item.setProductName(ci.getProductName());
            item.setQuantity(ci.getQuantity());
            item.setUnitPrice(ci.getPrice());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        OrderPayment payment = new OrderPayment();
        payment.setOrder(order);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaidAmount(0.0);

        OrderShipment shipment = new OrderShipment();
        shipment.setOrder(order);
        shipment.setShipmentStatus(ShipmentStatus.PENDING);

        order.setPayment(payment);
        order.setShipment(shipment);


        // Step 4: Save order
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
