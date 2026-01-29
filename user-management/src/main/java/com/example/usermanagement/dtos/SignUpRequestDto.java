package com.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {
    private String firstName;
    private String lastName;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}
