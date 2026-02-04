package com.example.productcatalog.services;

import com.example.productcatalog.exceptions.*;
import com.example.productcatalog.models.Product;
import com.example.productcatalog.repos.ProductRepo;
import com.example.productcatalog.repos.ProductSearchRepo;
import com.example.productcatalog.search.ProductSearchDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public Product createProduct(Product product) {
        if (product == null || product.getName() == null) {
            throw new InvalidRequestException("Product name is required");
        }

        Product savedProduct = productRepo.save(product);
        saveToElastic(savedProduct);
        return savedProduct;
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        Product existing = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setId(existing.getId());

        Product updatedProduct = productRepo.save(product);
        saveToElastic(updatedProduct);
        return updatedProduct;
    }

    private void saveToElastic(Product product) {
        ProductSearchDocument doc = new ProductSearchDocument();
        doc.setId(product.getId());
        doc.setName(product.getName());
        doc.setDescription(product.getDescription());
        doc.setCategory(product.getCategory() != null
                ? product.getCategory().getName()
                : null);
        doc.setPrice(product.getPrice());
        doc.setCreatedAt(product.getCreatedAt());
        searchRepo.save(doc);
    }
}
