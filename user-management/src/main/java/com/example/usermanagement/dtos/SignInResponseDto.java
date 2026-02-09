package com.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignInResponseDto {
    private Long userId;
    private String accessToken;
}
