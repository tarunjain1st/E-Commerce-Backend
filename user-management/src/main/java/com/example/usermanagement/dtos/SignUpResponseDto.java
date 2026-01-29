package com.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponseDto {
    private Long id;
    private String name;
    private String email;
}
