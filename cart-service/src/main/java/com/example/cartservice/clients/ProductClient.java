package com.example.cartservice.clients;

import com.example.cartservice.dtos.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
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
        ProductDto productDto = new ProductDto();
        productDto.setId(productId);
        productDto.setName("Iphone" + new Random().nextInt(10));
        productDto.setPrice(new Random().nextDouble(200000));
        return productDto;
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
