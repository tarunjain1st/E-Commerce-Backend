package com.example.paymentservice.controllers;

import com.example.paymentservice.models.PaymentGateway;
import com.example.paymentservice.services.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/webhook")
public class WebhookController {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/stripe")
    public void stripeWebhook(@RequestBody String payload,
                              @RequestHeader Map<String, String> headers) {
        paymentService.handleWebhook(PaymentGateway.STRIPE, payload, headers);
    }

    @PostMapping("/razorpay")
    public void razorpayWebhook(@RequestBody String payload,
                                @RequestHeader Map<String, String> headers) {
        paymentService.handleWebhook(PaymentGateway.RAZORPAY, payload, headers);
    }
}
