package com.example.payment.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CreateSessionDto {
    List<Long> amounts;
    List<Long> quantities;
    String successUrl;
    List<String> productNames;
}
