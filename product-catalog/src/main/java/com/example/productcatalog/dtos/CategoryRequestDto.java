package com.example.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequestDto {
    private String name;
    private String description;
    private Boolean isPremium;
}
