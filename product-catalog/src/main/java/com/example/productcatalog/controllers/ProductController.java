package com.example.productcatalog.controllers;

import com.example.productcatalog.dtos.CategoryDto;
import com.example.productcatalog.dtos.ProductDto;
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
public class ProductController {

    @Autowired
    //@Qualifier(value = "storageProductService")
    @Qualifier(value = "fakeStoreProductService")
    private IProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            productDtos.add(from(product));
        }
        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable(name = "id") Long productId){
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
        ProductDto productDto = from(product);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @PostMapping("/products")
    public ProductDto createProduct(@RequestBody ProductDto request){
        Product product = productService.createProduct(from(request));
        if (product == null) {
            return null;
        }
        return from(product);
    }

    @PutMapping("/products/{id}")
    public ProductDto updateProduct(@RequestBody ProductDto request, @PathVariable Long id){
        Product inputProduct = from(request);
        Product outputProduct = productService.updateProduct(id, inputProduct);
        if (outputProduct == null) {
                return null;
        }
        return from(outputProduct);
    }

    @GetMapping("/products/{productId}/{userId}")
    public ProductDto getProductDetailsBasedonUserScope(@PathVariable Long productId, @PathVariable Long userId){
        Product product = productService.getProductBasedOnUserScope(productId, userId);
        return from(product);
    }

    private ProductDto from(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setDescription(product.getDescription());
        productDto.setImageUrl(product.getImageUrl());
        if(product.getCategory() != null){
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(product.getCategory().getId());
            categoryDto.setName(product.getCategory().getName());
            categoryDto.setDescription(product.getCategory().getDescription());
            productDto.setCategory(categoryDto);
        }
        return productDto;
    }

    private Product from(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImageUrl(productDto.getImageUrl());
        if(productDto.getCategory() != null){
            Category category = new Category();
            category.setId(productDto.getCategory().getId());
            category.setName(productDto.getCategory().getName());
            product.setCategory(category);
        }
        return product;
    }
}
