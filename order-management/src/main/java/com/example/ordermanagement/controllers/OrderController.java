package com.example.ordermanagement.controllers;

import com.example.ordermanagement.dtos.*;
import com.example.ordermanagement.models.Order;
import com.example.ordermanagement.models.OrderAddress;
import com.example.ordermanagement.models.OrderStatus;
import com.example.ordermanagement.services.IOrderService;
import com.example.ordermanagement.utils.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    //Service call
    @GetMapping("/{orderId}")
    public PaymentOrderDto getOrderById(@PathVariable Long orderId) {
        System.out.println(orderId);
        Order order = orderService.getOrderById(orderId);
        System.out.println(order.getId());
        return OrderMapper.toPaymentOrderDto(order);
    }

    @GetMapping("/checkout")
    public List<OrderResponseDto> getOrders() {
        return orderService.getAllOrders()
                .stream()
                .map(OrderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/user")
    public List<OrderResponseDto> getOrdersByUserId(@RequestHeader("X-User-Id") Long userId) {
        return orderService.getOrdersByUserId(userId)
                .stream()
                .map(OrderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public OrderResponseDto createOrder(@RequestBody CreateOrderRequestDto dto) {
        OrderAddress address = OrderMapper.toOrderAddress(dto.getDeliveryAddress());
        Order order = orderService.createOrder(dto.getUserId(), dto.getCustomerName(), dto.getCustomerEmail(), address);
        return OrderMapper.toOrderResponse(order);
    }

    // Update order status (called by PaymentService)
    // PATCH /orders/42/status?status=CREATED
    @PatchMapping("/{orderId}/status")
    public OrderResponseDto updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        return OrderMapper.toOrderResponse(orderService.updateOrderStatus(orderId, status));
    }
}
