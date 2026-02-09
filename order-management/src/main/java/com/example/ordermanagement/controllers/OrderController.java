package com.example.ordermanagement.controllers;

import com.example.ordermanagement.dtos.*;
import com.example.ordermanagement.models.Order;
import com.example.ordermanagement.models.OrderAddress;
import com.example.ordermanagement.models.OrderStatus;
import com.example.ordermanagement.services.IOrderService;
import com.example.ordermanagement.utils.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentOrderDto> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(OrderMapper.toPaymentOrderDto(order));
    }

    @GetMapping("/checkout")
    public ResponseEntity<List<OrderResponseDto>> getOrders() {
        List<OrderResponseDto> list = orderService.getAllOrders()
                .stream()
                .map(OrderMapper::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@RequestHeader("X-User-Id") Long userId) {
        List<OrderResponseDto> list = orderService.getOrdersByUserId(userId)
                .stream()
                .map(OrderMapper::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody CreateOrderRequestDto dto) {
        OrderAddress address = OrderMapper.toOrderAddress(dto.getDeliveryAddress());
        Order order = orderService.createOrder(dto.getUserId(), dto.getCustomerName(), dto.getCustomerEmail(), address);
        return ResponseEntity.ok(OrderMapper.toOrderResponse(order));
    }

    // Update order status (called by PaymentService)
    // PATCH /orders/42/status?status=CREATED
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(OrderMapper.toOrderResponse(order));
    }
}
