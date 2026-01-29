package com.example.paymentservice.paymentgateways;

import com.example.paymentservice.models.PaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayChooserStrategy {

    @Autowired
    private StripePaymentGateway stripePaymentGateway;

    @Autowired
    private RazorpayPaymentGateway razorpayPaymentGateway;

    public IPaymentGateway getPaymentGateway(PaymentGateway gateway) {
        return switch (gateway) {
            case STRIPE -> stripePaymentGateway;
            case RAZORPAY -> razorpayPaymentGateway;
            default -> throw new IllegalArgumentException("Unsupported payment gateway: " + gateway);
        };
    }
}
