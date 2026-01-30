package com.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAddressResponseDto {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phone;
}
