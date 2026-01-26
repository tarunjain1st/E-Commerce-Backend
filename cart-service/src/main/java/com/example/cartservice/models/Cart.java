package com.example.cartservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Document(collection = "carts")
public class Cart{
    @Id
    private String id;

    private Long userId;

    private List<CartItem> items = new ArrayList<>();
}