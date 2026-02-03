package com.example.ordermanagement.consumers;
import com.example.ordermanagement.events.PaymentFailureEvent;
import com.example.ordermanagement.events.PaymentInitiatedEvent;
import com.example.ordermanagement.events.PaymentSuccessEvent;
import com.example.ordermanagement.services.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentsListener {
    @Autowired
    private OrderService orderService;
    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = "payment.success", groupId = "order-group")
    public void onPaymentSuccess(String msg) throws JsonProcessingException {
        PaymentSuccessEvent event = objectMapper.readValue(msg, PaymentSuccessEvent.class);
        orderService.handlePaymentSuccess(event.getOrderId(), event.getPaymentId(), event.getAmount());
    }

    @KafkaListener(topics = "payment.initiated", groupId = "order-group")
    public void onPaymentInitiated(String msg) throws JsonProcessingException {
        PaymentInitiatedEvent event = objectMapper.readValue(msg, PaymentInitiatedEvent.class);
        orderService.handlePaymentInitiated(event.getOrderId());
    }

    @KafkaListener(topics = "payment.failure", groupId = "order-group")
    public void onPaymentFailure(String msg) throws JsonProcessingException {
        PaymentFailureEvent event = objectMapper.readValue(msg, PaymentFailureEvent.class);
        orderService.handlePaymentFailure(event.getOrderId());
    }
}
