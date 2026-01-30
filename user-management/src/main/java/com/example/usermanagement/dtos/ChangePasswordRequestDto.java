package com.example.usermanagement.dtos;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String token;
    private String newPassword;
}
