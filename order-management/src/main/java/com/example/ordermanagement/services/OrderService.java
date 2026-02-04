package com.example.ordermanagement.services;

import com.example.ordermanagement.clients.CartClient;
import com.example.ordermanagement.clients.KafkaClient;
import com.example.ordermanagement.dtos.CartDto;
import com.example.ordermanagement.events.OrderPlacedEvent;
import com.example.ordermanagement.exceptions.*;
import com.example.ordermanagement.models.*;
import com.example.ordermanagement.repos.OrderRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaClient kafkaClient;

    @Override
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepo.findByUserId(userId);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public Order createOrder(Long userId, String customerName, String customerEmail, OrderAddress deliveryAddress) {

        if (userId == null || userId <= 0) {
            throw new InvalidOrderRequestException("Invalid user id");
        }

        // CartClient will throw EmptyCartException if cart is empty
        CartDto cart = cartClient.getCartByUserId(userId);

        Order order = new Order();
        order.setUserId(userId);
        order.setCustomerEmail(customerEmail);
        order.setCustomerName(customerName);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(cart.getTotalPrice());

        Order finalOrder = order;
        List<OrderItem> orderItems = cart.getItems().stream().map(ci -> {
            OrderItem item = new OrderItem();
            item.setProductId(ci.getProductId());
            item.setProductName(ci.getProductName());
            item.setQuantity(ci.getQuantity());
            item.setUnitPrice(ci.getPrice());
            item.setOrder(finalOrder);
            return item;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        OrderPayment payment = new OrderPayment();
        payment.setOrder(order);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(new Date());
        payment.setPaidAmount(0.0);
        payment.setUpdatedAt(new Date());

        OrderShipment shipment = new OrderShipment();
        shipment.setOrder(order);
        shipment.setShipmentStatus(ShipmentStatus.PENDING);
        shipment.setCreatedAt(new Date());
        shipment.setUpdatedAt(new Date());

        order.setPayment(payment);
        order.setShipment(shipment);

        order = orderRepo.save(order);

        try {
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderId(order.getId());
            orderPlacedEvent.setUserId(userId);
            orderPlacedEvent.setName(customerName);
            orderPlacedEvent.setEmail(customerEmail);
            orderPlacedEvent.setTotalAmount(cart.getTotalPrice());
            kafkaClient.sendMessage("order.placed", objectMapper.writeValueAsString(orderPlacedEvent));
        } catch (Exception ex) {
            throw new InvalidOrderRequestException(ex.getMessage());
        }

        return orderRepo.save(order);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepo.save(order);
    }

    @Override
    public void handlePaymentSuccess(Long orderId, String transactionId, Double amount) {
        Order order = getOrderById(orderId);
        OrderPayment payment = order.getPayment();
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(new Date());
        payment.setPaidAmount(amount);
        payment.setTransactionId(transactionId);
        payment.setUpdatedAt(new Date());
        orderRepo.save(order);
    }

    @Override
    public void handlePaymentInitiated(Long orderId) {
        Order order = getOrderById(orderId);
        OrderPayment payment = order.getPayment();
        payment.setPaymentStatus(PaymentStatus.INITIATED);
        payment.setUpdatedAt(new Date());
        orderRepo.save(order);
    }

    @Override
    public void handlePaymentFailure(Long orderId) {
        Order order = getOrderById(orderId);
        OrderPayment payment = order.getPayment();
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setUpdatedAt(new Date());
        orderRepo.save(order);
    }
}
