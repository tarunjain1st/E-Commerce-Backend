package com.example.productcatalog.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends BaseModel{
    private String name;
    private String description;
    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;
    private Double price;
    private String imageUrl;
    private Boolean isPrimeSpecific;
}
