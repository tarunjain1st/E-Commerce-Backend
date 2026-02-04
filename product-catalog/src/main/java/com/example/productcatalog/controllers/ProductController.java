package com.example.productcatalog.controllers;

import com.example.productcatalog.dtos.CategoryResponseDto;
import com.example.productcatalog.dtos.ProductRequestDto;
import com.example.productcatalog.dtos.ProductResponseDto;
import com.example.productcatalog.exceptions.InvalidRequestException;
import com.example.productcatalog.models.Category;
import com.example.productcatalog.models.Product;
import com.example.productcatalog.services.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    @Qualifier("storageProductService")
    private IProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponseDto> response = new ArrayList<>();

        for (Product product : products) {
            response.add(from(product));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable Long productId) {

        if (productId <= 0) {
            throw new InvalidRequestException("Product id must be greater than zero");
        }

        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(from(product));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @RequestBody ProductRequestDto request) {

        Product created = productService.createProduct(from(request));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(from(created));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @RequestBody ProductRequestDto request,
            @PathVariable Long productId) {

        if (productId <= 0) {
            throw new InvalidRequestException("Product id must be greater than zero");
        }

        Product updated = productService.updateProduct(productId, from(request));
        return ResponseEntity.ok(from(updated));
    }

    // -------------------- MAPPERS --------------------
    private ProductResponseDto from(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());

        if (product.getCategory() != null) {
            CategoryResponseDto categoryDto = new CategoryResponseDto();
            categoryDto.setId(product.getCategory().getId());
            categoryDto.setName(product.getCategory().getName());
            categoryDto.setDescription(product.getCategory().getDescription());
            categoryDto.setIsPremium(product.getCategory().getIsPremium());
            dto.setCategory(categoryDto);
        }

        return dto;
    }

    private Product from(ProductRequestDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());

        if (dto.getCategory() != null) {
            Category category = new Category();
            category.setName(dto.getCategory().getName());
            category.setDescription(dto.getCategory().getDescription());
            category.setIsPremium(dto.getCategory().getIsPremium());
            product.setCategory(category);
        }

        return product;
    }
}
