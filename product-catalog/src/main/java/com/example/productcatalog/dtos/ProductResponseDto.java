package com.example.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private CategoryResponseDto category;
    private Double price;
    private String imageUrl;
}
