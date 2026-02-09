package com.example.ordermanagement.clients;

import com.example.ordermanagement.dtos.CartDto;
import com.example.ordermanagement.exceptions.EmptyCartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CartClient {

    @Autowired
    private RestTemplate restTemplate;

    public CartDto getCartByUserId(Long userId) {
        String url = "http://cart-service/api/carts";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());
        headers.set("X-Internal-Call", "true");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<CartDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    CartDto.class
            );

            CartDto cart = response.getBody();

            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new EmptyCartException(userId);
            }

            return cart;

        } catch (RestClientException ex) {
            throw new RuntimeException("Failed to fetch cart for user " + userId, ex);
        }
    }
//            private <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
//            RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
//            ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
//            return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
//        }

}
