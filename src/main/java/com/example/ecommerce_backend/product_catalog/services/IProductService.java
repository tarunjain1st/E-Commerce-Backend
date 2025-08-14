package com.example.ecommerce_backend.product_catalog.services;

import com.example.ecommerce_backend.product_catalog.models.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
}
