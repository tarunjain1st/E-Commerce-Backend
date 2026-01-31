package com.example.productcatalog.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Inventory extends BaseModel{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private Integer totalQuantity;
    private Integer reservedQuantity;

}
