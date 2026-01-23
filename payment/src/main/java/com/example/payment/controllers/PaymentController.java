package com.example.payment.controllers;

import com.example.payment.dtos.PaymentRequestDto;
import com.example.payment.services.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping
    public String initiatePaymentRequest(@RequestBody PaymentRequestDto paymentRequestDto) {
        return paymentService.initiatePayment(paymentRequestDto.getAmount(), paymentRequestDto.getOrderId(), paymentRequestDto.getName(), paymentRequestDto.getEmail(), paymentRequestDto.getPhoneNumber(), paymentRequestDto.getDescription());
    }
}
