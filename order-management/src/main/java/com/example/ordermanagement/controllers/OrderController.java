package com.example.ordermanagement.controllers;


import java.util.UUID;

import com.example.ordermanagement.dtos.OrderRequestDto;
import com.example.ordermanagement.models.Order;
import com.example.ordermanagement.services.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    //Please add APIs here
    @PostMapping
    public Order createOrder(@RequestBody OrderRequestDto requestDto){
        Order order = orderService.createOrder(requestDto.getCustomerId(), requestDto.getTotalAmount());
        return order;
    }
    @DeleteMapping("/{orderId}")
    public Boolean deleteOrder(@PathVariable UUID orderId){
        return orderService.deleteOrder(orderId);
    }
}