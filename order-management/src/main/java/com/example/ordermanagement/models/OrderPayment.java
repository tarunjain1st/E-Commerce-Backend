package com.example.ordermanagement.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class OrderPayment extends BaseModel{

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String transactionId;
    private Double paidAmount;
    private Date paidAt;
}
