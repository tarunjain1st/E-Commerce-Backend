package com.example.cartservice.models;

import lombok.Data;

import java.util.*;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private Date date;
    private Map<Long,Double> products= new HashMap<>();
}
