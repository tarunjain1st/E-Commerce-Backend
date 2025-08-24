package com.example.ecommerce_backend.product_catalog.repos;

import com.example.ecommerce_backend.product_catalog.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepoTest {
    @Autowired
    private ProductRepo productRepo;

    @Test
    public void testQueries(){
         List<Product> productList = productRepo.findProductByPriceBetween(1000D, 10000D);
         for(Product product : productList){
             System.out.println(product.getName());
         }

         List<Product> productList2 = productRepo.findAllByOrderByPriceDesc();
         for(Product product : productList2){
             System.out.println(product.getName());
         }
    }

    @Test
    public void testQueryFindById() {
        String name = productRepo.testQueryFuncGetNameForProductId(1L);
        System.out.println(name);
    }
}