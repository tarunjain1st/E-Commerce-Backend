package com.example.cartservice.services;

import com.example.cartservice.clients.ProductClient;
import com.example.cartservice.dtos.ProductDto;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;
import com.example.cartservice.models.CartItemData;
import com.example.cartservice.repos.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class StorageCartService implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductClient productClient;

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setItems(new ArrayList<>());
                    cart.setTotalPrice(0.0);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Cart addItemToCart(Long userId, CartItemData item) {

        Cart cart = getCartByUserId(userId);

        ProductDto product = productClient.getProductById(item.getProductId());

        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(item.getProductId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setProductId(product.getId());
            cartItem.setProductName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(item.getQuantity());
            cart.getItems().add(cartItem);
        }

        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateItemInCart(Long userId, CartItemData item) {

        Cart cart = getCartByUserId(userId);

        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(item.getProductId()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Product not found in cart")
                );

        if (item.getQuantity() <= 0) {
            cart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(item.getQuantity());
        }

        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemFromCart(Long userId, Long productId) {

        Cart cart = getCartByUserId(userId);

        boolean removed = cart.getItems()
                .removeIf(ci -> ci.getProductId().equals(productId));

        if (!removed) {
            throw new RuntimeException("Product not found in cart");
        }

        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long userId) {

        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

    /* ================= helper ================= */

    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }
}
