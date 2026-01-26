package com.example.cartservice.clients;

import com.example.cartservice.dtos.FakeStoreCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class FakeStoreClient {

    @Autowired
    private RestTemplate restTemplate;

    public FakeStoreCart getCartById(Long cartId) {
        //Add your implementation here
        String url = "https://fakestoreapi.com/carts/{cartId}";
        ResponseEntity<FakeStoreCart> responseEntity = requestForEntity(HttpMethod.GET, url, null, FakeStoreCart.class, cartId);
        return responseEntity.getBody();
    }

    public FakeStoreCart[] getCartsByUserId(Long userId) {
        //Add your implementation here
        String url = "https://fakestoreapi.com/carts/user/{userId}";
        ResponseEntity<FakeStoreCart[]> responseEntity = requestForEntity(HttpMethod.GET, url, null, FakeStoreCart[].class, userId);
        return responseEntity.getBody();
    }

    public FakeStoreCart deleteCartById(Long cartId) {
        //Add your implementation here
        String url = "https://fakestoreapi.com/carts/{cartId}";
        ResponseEntity<FakeStoreCart> responseEntity = requestForEntity(HttpMethod.DELETE, url, null, FakeStoreCart.class, cartId);
        return responseEntity.getBody();
    }

    public FakeStoreCart updateCart(Long cartId,FakeStoreCart request) {
        //Add your implementation here
        String url = "https://fakestoreapi.com/carts/{cartId}";
        ResponseEntity<FakeStoreCart> responseEntity = requestForEntity(HttpMethod.PUT, url, request, FakeStoreCart.class, cartId);
        return responseEntity.getBody();
    }

    public FakeStoreCart addCart(FakeStoreCart request) {
        //Add your implementation here
        String url = "https://fakestoreapi.com/carts";
        ResponseEntity<FakeStoreCart> responseEntity = requestForEntity(HttpMethod.POST, url, request, FakeStoreCart.class);
        return responseEntity.getBody();
    }
    private <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
    }
}
