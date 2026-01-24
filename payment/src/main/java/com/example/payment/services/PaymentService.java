package com.example.payment.services;

import com.example.payment.dtos.SessionDto;
import com.example.payment.paymentgateways.IPaymentGateway;
import com.example.payment.paymentgateways.PaymentGatewayChooserStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService implements IPaymentService{

    @Autowired
    private PaymentGatewayChooserStrategy paymentGatewayChooserStrategy;

    @Override
    public String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description) {
        IPaymentGateway paymentGateway = paymentGatewayChooserStrategy.getPaymentGateway();
        return paymentGateway.initiatePayment(amount, orderId, name, email, phoneNumber, description);
    }

    @Override
    public SessionDto createSession(String successUrl, List<Long> amounts, List<String> productNames, List<Long> quantities) {
        IPaymentGateway paymentGateway = paymentGatewayChooserStrategy.getPaymentGateway();
        return paymentGateway.createSession(successUrl, amounts, productNames, quantities);
    }
}
