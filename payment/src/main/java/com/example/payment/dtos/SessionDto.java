package com.example.payment.dtos;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SessionDto {
    String id;
    Long total;
    Long expiry;
    String url;
}
