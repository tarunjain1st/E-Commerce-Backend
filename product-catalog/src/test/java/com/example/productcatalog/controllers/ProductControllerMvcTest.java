package com.example.productcatalog.controllers;

import com.example.productcatalog.dtos.ProductResponseDto;
import com.example.productcatalog.models.Product;
import com.example.productcatalog.services.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    @Qualifier(value = "storageProductService")
    IProductService productService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void TestGetAllProductsEndpoint_ReturnOK() throws Exception {
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }

    @Test
    public void TestGetAllProductsEndpoint_RunSuccessfully() throws Exception {
        //Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("iphone 16");
        product1.setPrice(1000000D);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("iphone 17");
        product2.setPrice(1500000D);

        List<Product> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);

        when(productService.getAllProducts()).thenReturn(productList);

        ProductResponseDto productResponseDto1 = new ProductResponseDto();
        productResponseDto1.setId(1L);
        productResponseDto1.setName("iphone 16");
        productResponseDto1.setPrice(1000000D);

        ProductResponseDto productResponseDto2 = new ProductResponseDto();
        productResponseDto2.setId(2L);
        productResponseDto2.setName("iphone 17");
        productResponseDto2.setPrice(1500000D);
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();
        productResponseDtoList.add(productResponseDto1);
        productResponseDtoList.add(productResponseDto2);

        String expectedResponse = objectMapper.writeValueAsString(productResponseDtoList);

        //Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
     public void TestCreateProductEndpoint_RunSuccessfully() throws Exception {
        ProductResponseDto productResponseDto1 = new ProductResponseDto();
        productResponseDto1.setId(1L);
        productResponseDto1.setName("iphone 16");
        productResponseDto1.setPrice(1000000D);

        Product  product1 = new Product();
        product1.setId(1L);
        product1.setName("iphone 16");
        product1.setPrice(1000000D);

        when(productService.createProduct(any(Product.class))).thenReturn(product1);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productResponseDto1)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(productResponseDto1)));
    }

}
