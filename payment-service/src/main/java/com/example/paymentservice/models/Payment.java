package com.example.paymentservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String paymentReference; // Stripe session or PaymentIntent ID

    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Double amount; // in cents
    private String customerEmail;

    private Date createdDate;
    private Date updatedDate;
}
