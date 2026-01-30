package com.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserProfileRequestDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String imageUrl;
    private UserAddressRequestDto address;
}
