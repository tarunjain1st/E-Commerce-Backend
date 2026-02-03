package com.example.ordermanagement.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "orders")
public class Order extends BaseModel {

    private String orderNumber;
    private Long userId;
    private Double totalAmount;
    private String customerEmail;
    private String customerName;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    private OrderAddress deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // 1 Order → 1 Payment
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderPayment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderShipment shipment;


}

