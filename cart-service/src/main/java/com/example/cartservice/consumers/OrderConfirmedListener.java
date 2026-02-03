package com.example.cartservice.consumers;

import com.example.cartservice.events.OrderConfirmedEvent;
import com.example.cartservice.services.ICartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedListener {
    @Autowired
    private ICartService cartService;
    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = "order.placed", groupId = "cart-group")
    public void onOrderConfirmed(String msg) throws JsonProcessingException {
        OrderConfirmedEvent event = objectMapper.readValue(msg, OrderConfirmedEvent.class);
        cartService.handleOrderConfirmed(event.getUserId());
    }
}
