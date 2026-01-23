package com.example.payment.paymentgateways;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayChooserStrategy {

    @Autowired
    private RazorpayPaymentGateway razorpayPaymentGateway;

    public IPaymentGateway getPaymentGateway() {
        return razorpayPaymentGateway;
    }
}
