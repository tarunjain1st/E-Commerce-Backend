package com.example.paymentservice.clients;

import com.example.paymentservice.dtos.OrderDto;
import com.example.paymentservice.exceptions.OrderNotFoundException;
import com.example.paymentservice.exceptions.PaymentGatewayUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClient {

    @Autowired
    private RestTemplate restTemplate;

    public OrderDto getOrderById(Long orderId) {
        String url = "http://order-management/api/orders/{orderId}";
        try {
            ResponseEntity<OrderDto> response = restTemplate.getForEntity(url, OrderDto.class, orderId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new OrderNotFoundException(orderId);
            }

            return response.getBody();

        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            throw new OrderNotFoundException(orderId);
        } catch (Exception e) {
            throw new PaymentGatewayUnavailableException("Order Service unavailable", e);
        }
    }
}
