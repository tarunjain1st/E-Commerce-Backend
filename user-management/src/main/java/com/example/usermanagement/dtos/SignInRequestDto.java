package com.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequestDto {
    private String email;
    private String password;
}
