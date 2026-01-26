package com.example.cartservice.services;

import com.example.cartservice.clients.FakeStoreClient;
import com.example.cartservice.dtos.FakeStoreCart;
import com.example.cartservice.dtos.FakeStoreProduct;
import com.example.cartservice.models.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FakeStoreCartService implements ICartService {

    @Autowired
    private FakeStoreClient fakeStoreClient;

    public Cart getCartById(Long cartId) {
        //Add your implementation here
        return from(fakeStoreClient.getCartById(cartId));
    }

    public List<Cart> getCartByUserId(Long userId) {
        //Add your implementation here
        List<Cart> carts = new ArrayList<>();
        for(FakeStoreCart fakeStoreCart : fakeStoreClient.getCartsByUserId(userId)){
            carts.add(from(fakeStoreCart));
        }
        return carts;
    }

    public Cart deleteCartById(Long cartId) {
        //Add your implementation here
        return from(fakeStoreClient.deleteCartById(cartId));
    }

    public Cart updateCart(Long cartId,Cart request) {
        //Add your implementation here
        return from(fakeStoreClient.updateCart(cartId, from(request)));
    }

    public Cart addCart(Cart request) {
        //Add your implementation here
        return from(fakeStoreClient.addCart(from(request)));
    }

    private FakeStoreCart from(Cart cart) {
        FakeStoreCart fakeStoreCart = new FakeStoreCart();
        fakeStoreCart.setId(cart.getId());
        Instant instant = cart.getDate().toInstant();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        fakeStoreCart.setDate(formatter.format(instant));
        fakeStoreCart.setUserId(cart.getUserId());
        if(cart.getProducts() != null) {
            List<FakeStoreProduct> fakeStoreProducts = new ArrayList<>();
            for (Map.Entry<Long, Double> product : cart.getProducts().entrySet()) {
                Long productId = product.getKey();
                Double quantity = product.getValue();
                FakeStoreProduct fakeStoreProduct = new FakeStoreProduct();
                fakeStoreProduct.setProductId(productId);
                fakeStoreProduct.setQuantity(quantity);
                fakeStoreProducts.add(fakeStoreProduct);
            }
            fakeStoreCart.setProducts(fakeStoreProducts);
        }
        return fakeStoreCart;
    }

    private Cart from(FakeStoreCart fakeStoreCart) {
        Cart cart = new Cart();
        cart.setId(fakeStoreCart.getId());
        cart.setUserId(fakeStoreCart.getUserId());
        Instant instant = Instant.parse(fakeStoreCart.getDate());
        cart.setDate(Date.from(instant));
        if(fakeStoreCart.getProducts() != null) {
            Map<Long,Double> products = new HashMap<>();
            for (FakeStoreProduct fakeStoreProduct : fakeStoreCart.getProducts()) {
                products.put(fakeStoreProduct.getProductId(),fakeStoreProduct.getQuantity());
            }
            cart.setProducts(products);
        }
        return cart;
    }
}
