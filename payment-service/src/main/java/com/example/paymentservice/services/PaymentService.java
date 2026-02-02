package com.example.paymentservice.services;

import com.example.paymentservice.clients.KafkaClient;
import com.example.paymentservice.clients.OrderClient;
import com.example.paymentservice.dtos.OrderDto;
import com.example.paymentservice.dtos.PaymentResponse;
import com.example.paymentservice.dtos.PaymentSession;
import com.example.paymentservice.dtos.PaymentWebhookResponse;
import com.example.paymentservice.events.PaymentSuccessEvent;
import com.example.paymentservice.models.Payment;
import com.example.paymentservice.models.PaymentGateway;
import com.example.paymentservice.models.PaymentStatus;
import com.example.paymentservice.paymentgateways.IPaymentGateway;
import com.example.paymentservice.paymentgateways.PaymentGatewayChooserStrategy;
import com.example.paymentservice.repos.PaymentRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.Map;

@Service
public class PaymentService implements IPaymentService{

    @Autowired
    private PaymentGatewayChooserStrategy gatewayChooser;
    @Autowired
    private PaymentRepo paymentRepo;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private KafkaClient  kafkaClient;
    @Autowired
    private ObjectMapper objectMapper;

//    @Override
//    public P initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description) {
//        IPaymentGateway paymentGateway = paymentGatewayChooserStrategy.getPaymentGateway();
//        return paymentGateway.initiatePayment(amount, orderId, name, email, phoneNumber, description);
//    }
//
//    @Override
//    public SessionDto createSession(String successUrl, List<Long> amounts, List<String> productNames, List<Long> quantities) {
//        IPaymentGateway paymentGateway = paymentGatewayChooserStrategy.getPaymentGateway();
//        return paymentGateway.createSession(successUrl, amounts, productNames, quantities);
//    }

    @Transactional
    public PaymentResponse createPayment(Long orderId, PaymentGateway gateway) {

        // Fetch order info from Order microservice
        OrderDto orderDto = orderClient.getOrderById(orderId);
        // Choose the payment gateway
        IPaymentGateway paymentGateway = gatewayChooser.getPaymentGateway(gateway);
        // Create Stripe session
        PaymentResponse response = paymentGateway.createPayment(orderDto);
        // Save payment info
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentGateway(gateway);
        payment.setAmount(orderDto.getTotalAmount());
        payment.setCustomerEmail(orderDto.getCustomerEmail());
        payment.setPaymentReference(response.getSessionId());
        payment.setCreatedDate(new Date());
        payment.setUpdatedDate(new Date());
        payment.setStatus(PaymentStatus.INITIATED);
        paymentRepo.save(payment);
        //kafkaClient.sendMessage("payment.initiated", objectMapper.writeValueAsString(orderPlacedEvent));
        return response;
    }

    @Override
    public Boolean verifyPaymentStatus(String sessionId, PaymentGateway gateway) {
        IPaymentGateway paymentGateway = gatewayChooser.getPaymentGateway(gateway);
        PaymentSession paymentSession = paymentGateway.getPaymentDetails(sessionId);
        Payment payment = paymentRepo.findByPaymentReference(paymentSession.getSessionId()).get();
        if(paymentSession.getStatus().equals("paid")){
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

        if(paymentWebhookResponse == null){
            return;
        }

        Payment payment = paymentRepo.findByPaymentReference(paymentWebhookResponse.getPaymentReference())
                .orElseThrow();

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return; // idempotent: already processed
        }

        payment.setStatus(paymentWebhookResponse.getStatus());
        payment.setUpdatedDate(new Date());

        paymentRepo.save(payment);

        if (paymentWebhookResponse.getStatus() == PaymentStatus.SUCCESS) {
            try {
                PaymentSuccessEvent paymentSuccessEvent = new PaymentSuccessEvent();
                paymentSuccessEvent.setPaymentId(payment.getPaymentReference());
                paymentSuccessEvent.setEmail(payment.getCustomerEmail());
                paymentSuccessEvent.setAmount(payment.getAmount());
                paymentSuccessEvent.setName(payment.getCustomerEmail());
                kafkaClient.sendMessage("payment.success", objectMapper.writeValueAsString(paymentSuccessEvent));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else if (paymentWebhookResponse.getStatus() == PaymentStatus.FAILED) {
            //kafkaClient.sendMessage("payment.failure", objectMapper.writeValueAsString(orderPlacedEvent));
        }
    }
}
