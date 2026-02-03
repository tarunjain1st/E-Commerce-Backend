package com.example.paymentservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"payment_reference"})
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    @Column(name = "payment_reference", nullable = false, unique = true)
    private String paymentReference; // Stripe session or PaymentIntent ID

    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Double amount; // in cents
    private String customerName;
    private String customerEmail;

    private Date createdDate;
    private Date updatedDate;
}
