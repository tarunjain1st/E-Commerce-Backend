package com.example.ordermanagement.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Embeddable
public class OrderAddress{
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
}


