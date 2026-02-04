package com.example.paymentservice.services;

import com.example.paymentservice.clients.KafkaClient;
import com.example.paymentservice.clients.OrderClient;
import com.example.paymentservice.dtos.OrderDto;
import com.example.paymentservice.dtos.PaymentResponse;
import com.example.paymentservice.dtos.PaymentSession;
import com.example.paymentservice.dtos.PaymentWebhookResponse;
import com.example.paymentservice.events.PaymentFailureEvent;
import com.example.paymentservice.events.PaymentInitiatedEvent;
import com.example.paymentservice.events.PaymentSuccessEvent;
import com.example.paymentservice.exceptions.PaymentGatewayUnavailableException;
import com.example.paymentservice.exceptions.PaymentInitiationException;
import com.example.paymentservice.models.Payment;
import com.example.paymentservice.models.PaymentGateway;
import com.example.paymentservice.models.PaymentStatus;
import com.example.paymentservice.paymentgateways.IPaymentGateway;
import com.example.paymentservice.paymentgateways.PaymentGatewayChooserStrategy;
import com.example.paymentservice.repos.PaymentRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentGatewayChooserStrategy gatewayChooser;
    @Autowired
    private PaymentRepo paymentRepo;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private KafkaClient kafkaClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public PaymentResponse createPayment(Long orderId, PaymentGateway gateway) {

        // Fetch order info
        OrderDto orderDto = orderClient.getOrderById(orderId);

        // Choose gateway
        IPaymentGateway paymentGateway;
        try {
            paymentGateway = gatewayChooser.getPaymentGateway(gateway);
        } catch (Exception e) {
            throw new PaymentGatewayUnavailableException("Payment gateway not available: " + gateway, e);
        }

        // Create payment session
        PaymentResponse response;
        try {
            response = paymentGateway.createPayment(orderDto);
        } catch (Exception e) {
            throw new PaymentInitiationException("Failed to initiate payment", e);
        }

        // Save payment
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentGateway(gateway);
        payment.setAmount(orderDto.getTotalAmount());
        payment.setCustomerName(orderDto.getCustomerName());
        payment.setCustomerEmail(orderDto.getCustomerEmail());
        payment.setPaymentReference(response.getSessionId());
        payment.setCreatedDate(new Date());
        payment.setUpdatedDate(new Date());
        payment.setStatus(PaymentStatus.INITIATED);
        paymentRepo.save(payment);

        // Send event
        try {
            PaymentInitiatedEvent event = new PaymentInitiatedEvent();
            event.setOrderId(payment.getOrderId());
            kafkaClient.sendMessage("payment.initiated", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new PaymentInitiationException("Failed to send payment initiated event", e);
        }

        return response;
    }

    @Override
    public Boolean verifyPaymentStatus(String sessionId, PaymentGateway gateway) {
        IPaymentGateway paymentGateway = gatewayChooser.getPaymentGateway(gateway);
        PaymentSession paymentSession = paymentGateway.getPaymentDetails(sessionId);
        Payment payment = paymentRepo.findByPaymentReference(paymentSession.getSessionId())
                .orElseThrow(() -> new PaymentInitiationException("Payment not found for session: " + sessionId, null));

        if (paymentSession.getStatus().equals("paid")) {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepo.save(payment);
            return true;
        }
        return false;
    }

    @Transactional
    public void handleWebhook(PaymentGateway gateway, String payload, Map<String, String> headers) {
        IPaymentGateway paymentGateway = gatewayChooser.getPaymentGateway(gateway);
        PaymentWebhookResponse paymentWebhookResponse = paymentGateway.parseWebhook(payload, headers);

        if (paymentWebhookResponse == null) return;

        Payment payment = paymentRepo.findByPaymentReference(paymentWebhookResponse.getPaymentReference())
                .orElseThrow(() -> new PaymentInitiationException(
                        "Payment not found for reference: " + paymentWebhookResponse.getPaymentReference(), null));

        if (payment.getStatus() == PaymentStatus.SUCCESS) return; // already processed

        payment.setStatus(paymentWebhookResponse.getStatus());
        payment.setUpdatedDate(new Date());
        paymentRepo.save(payment);

        try {
            if (paymentWebhookResponse.getStatus() == PaymentStatus.SUCCESS) {
                PaymentSuccessEvent paymentSuccessEvent = new PaymentSuccessEvent();
                paymentSuccessEvent.setOrderId(payment.getOrderId());
                paymentSuccessEvent.setPaymentId(payment.getPaymentReference());
                paymentSuccessEvent.setEmail(payment.getCustomerEmail());
                paymentSuccessEvent.setAmount(payment.getAmount());
                paymentSuccessEvent.setName(payment.getCustomerName());
                kafkaClient.sendMessage("payment.success", objectMapper.writeValueAsString(paymentSuccessEvent));
            } else if (paymentWebhookResponse.getStatus() == PaymentStatus.FAILED) {
                PaymentFailureEvent event = new PaymentFailureEvent();
                event.setOrderId(payment.getOrderId());
                kafkaClient.sendMessage("payment.failure", objectMapper.writeValueAsString(event));
            }
        } catch (JsonProcessingException e) {
            throw new PaymentInitiationException("Failed to send payment event", e);
        }
    }
}
