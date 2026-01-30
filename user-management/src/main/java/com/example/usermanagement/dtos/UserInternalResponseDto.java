package com.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserInternalResponseDto {
    private Long id;
    private String email;
    private String phoneNumber;
    private String status;
    private List<UserAddressResponseDto> addresses;
}
