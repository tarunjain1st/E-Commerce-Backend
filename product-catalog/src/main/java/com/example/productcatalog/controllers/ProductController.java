package com.example.productcatalog.controllers;

import com.example.productcatalog.dtos.CategoryResponseDto;
import com.example.productcatalog.dtos.ProductRequestDto;
import com.example.productcatalog.dtos.ProductResponseDto;
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
    //@Qualifier(value = "fakeStoreProductService")
    @Qualifier(value = "storageProductService")
    private IProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (Product product : products) {
            productResponseDtos.add(from(product));
        }
        return new ResponseEntity<>(productResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable(name = "productId") Long productId){
        if(productId < 0){
            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            throw new IllegalArgumentException("Invalid product id");
        }
        else if(productId == 0){
            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            throw new IllegalArgumentException("Product id should be greater than zero");
        }
        Product product = productService.getProductById(productId);
        if (product == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        ProductResponseDto productResponseDto = from(product);
        return new ResponseEntity<>(productResponseDto, HttpStatus.OK);
    }

    @PostMapping
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto request){
        Product product = productService.createProduct(from(request));
        if (product == null) {
            return null;
        }
        return from(product);
    }

    @PutMapping("/{productId}")
    public ProductResponseDto updateProduct(@RequestBody ProductRequestDto request, @PathVariable Long productId){
        Product inputProduct = from(request);
        Product outputProduct = productService.updateProduct(productId, inputProduct);
        if (outputProduct == null) {
                return null;
        }
        return from(outputProduct);
    }

    private ProductResponseDto from(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setImageUrl(product.getImageUrl());
        if(product.getCategory() != null){
            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(product.getCategory().getId());
            categoryResponseDto.setName(product.getCategory().getName());
            categoryResponseDto.setDescription(product.getCategory().getDescription());
            categoryResponseDto.setIsPremium(product.getCategory().getIsPremium());
            productResponseDto.setCategory(categoryResponseDto);
        }
        return productResponseDto;
    }

    private Product from(ProductRequestDto productRequestDto) {
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setDescription(productRequestDto.getDescription());
        product.setImageUrl(productRequestDto.getImageUrl());
        if(productRequestDto.getCategory() != null){
            Category category = new Category();
            category.setName(productRequestDto.getCategory().getName());
            category.setDescription(productRequestDto.getCategory().getDescription());
            category.setIsPremium(productRequestDto.getCategory().getIsPremium());
            product.setCategory(category);
        }
        return product;
    }
}
