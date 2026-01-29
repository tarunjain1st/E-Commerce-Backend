package com.example.paymentservice.services;

import com.example.paymentservice.clients.OrderClient;
import com.example.paymentservice.dtos.OrderInfo;
import com.example.paymentservice.dtos.PaymentResponse;
import com.example.paymentservice.dtos.PaymentSession;
import com.example.paymentservice.models.Payment;
import com.example.paymentservice.models.PaymentGateway;
import com.example.paymentservice.models.PaymentStatus;
import com.example.paymentservice.paymentgateways.IPaymentGateway;
import com.example.paymentservice.paymentgateways.PaymentGatewayChooserStrategy;
import com.example.paymentservice.repos.PaymentRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PaymentService implements IPaymentService{

    @Autowired
    private PaymentGatewayChooserStrategy gatewayChooser;
    @Autowired
    private PaymentRepo paymentRepo;
    @Autowired
    private OrderClient orderClient;

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
        OrderInfo orderInfo = orderClient.getOrder(orderId);
        // Choose the payment gateway
        IPaymentGateway paymentGateway = gatewayChooser.getPaymentGateway(gateway);
        // Create Stripe session
        PaymentResponse response = paymentGateway.createPayment(orderInfo);
        // Save payment info
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentGateway(gateway);
        payment.setAmount(orderInfo.getTotalAmount());
        payment.setCustomerEmail(orderInfo.getCustomerEmail());
        payment.setPaymentReference(response.getSessionId());
        payment.setCreatedDate(new Date());
        payment.setUpdatedDate(new Date());
        payment.setStatus(PaymentStatus.CREATED);
        paymentRepo.save(payment);

        return response;
    }

    @Override
    public Boolean verifyPaymentStatus(String sessionId, PaymentGateway gateway) {
        IPaymentGateway paymentGateway = gatewayChooser.getPaymentGateway(gateway);
        PaymentSession paymentSession = paymentGateway.getPaymentDetails(sessionId);
        return paymentSession != null && paymentSession.getStatus().equals("paid");
    }
}
