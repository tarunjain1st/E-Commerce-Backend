package com.example.productcatalog.controllers;

import com.example.productcatalog.dtos.ProductDto;
import com.example.productcatalog.models.Product;
import com.example.productcatalog.services.IProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductControllerTest {

    @Autowired
    private ProductController productController;

    @MockitoBean
    @Qualifier(value = "storageProductService")
    private IProductService productService;

    @Test
    public void TestGetProductById_OnValidId_RunSuccessFully() {
        //Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Iphone");
        product.setPrice(150000D);
        when(productService.getProductById(productId)).thenReturn(product);

        //Act
        ResponseEntity<ProductDto> response = productController.getProductById(productId);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getId());
        assertEquals("Iphone", response.getBody().getName());
        assertEquals(150000D, response.getBody().getPrice());
    }

    @Test
    public void TestGetProductById_WithInvalidId_ResultsInIllegalArgumentException() {
        //Arrange
        Long  productId = -1L;

        //Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productController.getProductById(productId));
        assertEquals("Invalid product id", exception.getMessage());
    }

    @Test
    public void TestGetProductById_ServiceThrowsException_ResultsInSameException() {
        //Arrange
        Long  productId = 0L;
        when(productService.getProductById(productId)).thenThrow(new RuntimeException());

        //Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> productController.getProductById(productId));
        assertEquals("Product id should be greater than zero", exception.getMessage());
    }

    @Test
    public void TestCreateProduct_WithValidInput_RunSuccessFully() {
        //Arrange
        ProductDto productDto = new ProductDto();
        productDto.setId(10l);
        productDto.setName("Iphone 16");
        productDto.setPrice(150000D);

        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());

        when(productService.createProduct(any(Product.class))).thenReturn(product);

        //Act
        ProductDto result = productController.createProduct(productDto);

        //Assert
        assertNotNull(result);
        assertEquals(productDto.getId(), result.getId());
        assertEquals(productDto.getName(), result.getName());
    }
}