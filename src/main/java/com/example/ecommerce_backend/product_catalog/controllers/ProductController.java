package com.example.ecommerce_backend.product_catalog.controllers;

import com.example.ecommerce_backend.product_catalog.models.Product;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        products.add(product);
        return products;
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable(name = "id") Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("Product: " + id);
        return product;
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product){
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setCategory(product.getCategory());
        newProduct.setPrice(product.getPrice());
        return newProduct   ;
    }
}
