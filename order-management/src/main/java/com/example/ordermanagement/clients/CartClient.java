package com.example.ordermanagement.clients;

import com.example.ordermanagement.dtos.CartDto;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CartClient {

    @Autowired
    private RestTemplate restTemplate;

        public CartDto getCartByUserId(Long userId) {
            //Add your implementation here
            String url = "http://cart-service/api/carts/checkout";
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("X-User-Id", userId.toString());
            httpHeaders.set("X-Internal-Call", "true");
            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

            ResponseEntity<CartDto> responseEntity =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            CartDto.class
                    );
            return responseEntity.getBody();
        }
//        private <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
//            RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
//            ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
//            return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
//        }
}
