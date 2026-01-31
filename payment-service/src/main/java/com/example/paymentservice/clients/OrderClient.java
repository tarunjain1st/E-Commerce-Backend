package com.example.paymentservice.clients;

import com.example.paymentservice.dtos.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClient {

    @Autowired
    RestTemplate restTemplate;

    public OrderDto getOrderById(Long orderId) {
        String url = "http://order-management/api/orders/{orderId}";
        ResponseEntity<OrderDto> response = restTemplate.getForEntity(url, OrderDto.class, orderId);
        System.out.println(response.getBody().getOrderId());
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to fetch order " + orderId + " from Order Service");
        }

        return response.getBody();
    }
}
