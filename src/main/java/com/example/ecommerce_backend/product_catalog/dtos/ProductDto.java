package com.example.ecommerce_backend.product_catalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private CategoryDto categoryDto;
    private Double price;
    private String imageUrl;
}
