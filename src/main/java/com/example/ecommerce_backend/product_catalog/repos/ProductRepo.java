package com.example.ecommerce_backend.product_catalog.repos;

import com.example.ecommerce_backend.product_catalog.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findProductByPriceBetween(double low, double high);
    List<Product> findAllByOrderByPriceDesc();

//    @Query("SELECT p.name from Product p where p.id=?1")
    @Query("SELECT p.name from Product p where p.id=:productId")
    String testQueryFuncGetNameForProductId(Long productId);
}
