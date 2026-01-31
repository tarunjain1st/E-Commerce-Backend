package com.example.ordermanagement.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class OrderShipment extends BaseModel{
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private String carrier;             // e.g., DHL, FedEx
    private String trackingNumber;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;
    private Date shippedAt;
    private Date deliveredAt;
}
