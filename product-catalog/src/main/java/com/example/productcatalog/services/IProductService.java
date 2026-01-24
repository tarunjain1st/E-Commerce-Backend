package com.example.productcatalog.services;


import com.example.productcatalog.models.Product;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    Product getProductBasedOnUserScope(Long productId, Long userId);
}
