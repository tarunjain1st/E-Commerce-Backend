package com.example.cartservice.clients;

import com.example.cartservice.dtos.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Component
public class ProductClient {

    @Autowired
    private RestTemplate restTemplate;

    public ProductDto getProductById(Long productId){

        String url = "http://product-catalog/api/products/{productId}";

        try {
            ResponseEntity<ProductDto> response = restTemplate.getForEntity(url, ProductDto.class, productId);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Failed to fetch product " + productId);
            }
            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Product Service call failed for product " + productId, e);
        }

    }
    //    public ProductDto getProductById(Long productId) {
    //        //Add your implementation here
    //        String url = "https://fakestoreapi.com/carts/{cartId}";
    //        ResponseEntity<ProductDto> responseEntity = requestForEntity(HttpMethod.GET, url, null, ProductDto.class, productId);
    //        return responseEntity.getBody();
    //    }
    //    private <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
    //        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
    //        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
    //        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
    //    }
}
