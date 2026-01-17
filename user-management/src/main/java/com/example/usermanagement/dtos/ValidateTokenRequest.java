package com.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenRequest {
    private String token;
    private Long userId;
}
