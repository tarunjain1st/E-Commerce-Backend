package com.example.cartservice.dtos;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FakeStoreCart {
    private Long id;
    private Long userId;
    private String date;
    private List<FakeStoreProduct> products = new ArrayList<>();
}
