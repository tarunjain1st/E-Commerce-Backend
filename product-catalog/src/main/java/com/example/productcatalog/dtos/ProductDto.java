package com.example.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private CategoryDto category;
    private Double price;
    private String imageUrl;
}
