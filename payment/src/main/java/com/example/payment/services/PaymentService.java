package com.example.payment.services;

import com.example.payment.paymentgateways.IPaymentGateway;
import com.example.payment.paymentgateways.PaymentGatewayChooserStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements IPaymentService{

    @Autowired
    private PaymentGatewayChooserStrategy paymentGatewayChooserStrategy;

    @Override
    public String initiatePayment(Long amount, String orderId, String name, String email, String phoneNumber, String description) {
        IPaymentGateway paymentGateway = paymentGatewayChooserStrategy.getPaymentGateway();
        return paymentGateway.initiatePayment(amount, orderId, name, email, phoneNumber, description);
    }
}
