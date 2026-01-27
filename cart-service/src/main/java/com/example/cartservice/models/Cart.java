package com.example.cartservice.models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Document(collection = "carts")
public class Cart{
    @Id
    private String id;
    private Long userId;
    private Double totalPrice;
    private List<CartItem> items = new ArrayList<>();
    @CreatedDate
    private Date creationDate;
    @LastModifiedDate
    private Date lastUpdateDate;
}