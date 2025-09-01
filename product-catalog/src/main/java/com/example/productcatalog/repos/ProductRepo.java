package com.example.productcatalog.repos;


import com.example.productcatalog.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findProductByPriceBetween(double low, double high);
    List<Product> findAllByOrderByPriceDesc();

//    @Query("SELECT p.name from Product p where p.id=?1")
    @Query("SELECT p.name from Product p where p.id=:productId")
    String testQueryFuncGetNameForProductId(Long productId);
}
