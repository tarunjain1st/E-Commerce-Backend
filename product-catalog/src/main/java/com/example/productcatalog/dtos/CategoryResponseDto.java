package com.example.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean isPremium;
}
