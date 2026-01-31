package com.example.ordermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderAddressDto {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
}

