package com.example.productcatalog.services;

import com.example.productcatalog.models.Product;
import com.example.productcatalog.repos.ProductRepo;
import com.example.productcatalog.repos.ProductSearchRepo;
import com.example.productcatalog.search.ProductSearchDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service(value = "storageProductService")
public class StorageProductService implements IProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ProductSearchRepo searchRepo;

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepo.findById(productId);
        return productOptional.orElse(null);
    }

    @Override
    public Product createProduct(Product product) {

        Product savedProduct = productRepo.save(product);

        // Index in Elasticsearch
        saveToElastic(savedProduct);
        return savedProduct;
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        Product updatedProduct = productRepo.save(product);

        // Update in Elasticsearch
        saveToElastic(updatedProduct);

        return updatedProduct;
    }

    // -----------------------------
    // Helper: save product to Elasticsearch
    // -----------------------------
    private void saveToElastic(Product product) {
        ProductSearchDocument doc = new ProductSearchDocument();
        doc.setId(product.getId());
        doc.setName(product.getName());
        doc.setDescription(product.getDescription());
        doc.setCategory(product.getCategory() != null ? product.getCategory().getName() : null);
        doc.setPrice(product.getPrice());
        doc.setCreatedAt(product.getCreatedAt());
        searchRepo.save(doc);
    }
}
