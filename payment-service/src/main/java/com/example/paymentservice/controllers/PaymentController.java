package com.example.paymentservice.controllers;

import com.example.paymentservice.dtos.PaymentRequestDto;
import com.example.paymentservice.dtos.PaymentResponse;
import com.example.paymentservice.dtos.PaymentResponseDto;
import com.example.paymentservice.models.PaymentGateway;
import com.example.paymentservice.services.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

//    @PostMapping
//    public String initiatePaymentRequest(@RequestBody PaymentRequestDto paymentRequestDto) {
//        return paymentService.initiatePayment(paymentRequestDto.getAmount(), paymentRequestDto.getOrderId(), paymentRequestDto.getName(), paymentRequestDto.getEmail(), paymentRequestDto.getPhoneNumber(), paymentRequestDto.getDescription());
//    }
//
//    @PostMapping("/checkout")
//    public SessionDto createSession(@RequestBody CreateSessionDto createSessionDto){
//        return paymentService.createSession(createSessionDto.getAmounts(), createSessionDto.getProductNames(), createSessionDto.getQuantities());
//    }

    @PostMapping("/checkout")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto requestDto) {
        System.out.println("inside createPayment");
        // Call service to create payment and Stripe session
        PaymentResponse response = paymentService.createPayment(
                requestDto.getOrderId(),
                requestDto.getPaymentGateway()
        );
        System.out.println("after payment response");
        // Map to controller response DTO
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setSessionId(response.getSessionId());
        responseDto.setCheckoutUrl(response.getCheckoutUrl());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam("session_id") String sessionId,
            @RequestParam("gateway") PaymentGateway gateway) { // Specify gateway in request

        boolean paid = paymentService.verifyPaymentStatus(sessionId, gateway);

        if (paid) {
            return ResponseEntity.ok("Payment successful! Thank you for your order.");
        } else {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                    .body("Payment failed or incomplete. Please try again.");
        }
    }
}
