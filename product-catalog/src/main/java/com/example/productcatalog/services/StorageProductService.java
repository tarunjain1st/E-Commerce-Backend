package com.example.productcatalog.services;

import com.example.productcatalog.dtos.UserDto;
import com.example.productcatalog.models.Product;
import com.example.productcatalog.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service(value = "storageProductService")
public class StorageProductService implements IProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepo.findById(productId);
        if (productOptional.isEmpty()) {
            return null;
        }
        return productOptional.get();
    }

    @Override
    public Product createProduct(Product product) {
        Optional<Product> productOptional = productRepo.findById(product.getId());
        if (productOptional.isPresent()) {
            return productOptional.get();
        }
        return productRepo.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        return productRepo.save(product);
    }

    @Override
    public Product getProductBasedOnUserScope(Long productId, Long userId) {
        Optional<Product> productOptional = productRepo.findById(productId);
        if (productOptional.isPresent()) {
            //Call User Service
            UserDto userDto = restTemplate.getForEntity("http://user-management/users/{userId}", UserDto.class, userId).getBody();
            if (userDto != null) {
                System.out.println(userDto.getEmail());
                return productOptional.get();
            }
        }
        return null;
    }
}
