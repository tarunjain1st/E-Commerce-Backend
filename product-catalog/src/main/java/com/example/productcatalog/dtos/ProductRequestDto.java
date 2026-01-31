package com.example.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductRequestDto {
    private String name;
    private String description;
    private CategoryRequestDto category;
    private Double price;
    private String imageUrl;
}
