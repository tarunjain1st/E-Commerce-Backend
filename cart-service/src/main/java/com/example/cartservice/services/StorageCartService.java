package com.example.cartservice.services;

import com.example.cartservice.clients.ProductClient;
import com.example.cartservice.dtos.ProductDto;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;
import com.example.cartservice.models.CartStatus;
import com.example.cartservice.repos.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class StorageCartService implements ICartService {

    private static final Duration CART_TTL = Duration.ofHours(1); // TTL as per PRD (can be configured)

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String redisKey(Long userId) {
        return "cart:" + userId;
    }

    @Override
    public Cart getActiveCart(Long userId) {

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String key = redisKey(userId);

        // Check Redis cache first
        Cart cart = (Cart) ops.get(key);
        if (cart != null) return cart;

        // Fallback to MongoDB
        cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setItems(new ArrayList<>());
                    newCart.setTotalPrice(0.0);
                    newCart.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(newCart);
                });

        // Cache in Redis with TTL
        ops.set(key, cart, CART_TTL);

        return cart;
    }

    @Override
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {

        Cart cart = getActiveCart(userId);

        ProductDto product = productClient.getProductById(productId);

        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProductId(product.getId());
            cartItem.setProductName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }

        recalculateTotal(cart);

        // Persist to MongoDB
        cartRepository.save(cart);

        // Update Redis cache with TTL
        redisTemplate.opsForValue().set(redisKey(userId), cart, CART_TTL);

        return cart;
    }

    @Override
    public Cart removeItemFromCart(Long userId, Long productId) {

        Cart cart = getActiveCart(userId);

        boolean removed = cart.getItems().removeIf(ci -> ci.getProductId().equals(productId));
        if (!removed) throw new RuntimeException("Product not found in cart");

        recalculateTotal(cart);

        // Persist & cache
        cartRepository.save(cart);
        redisTemplate.opsForValue().set(redisKey(userId), cart, CART_TTL);

        return cart;
    }

    @Override
    public void clearCart(Long userId) {

        Cart cart = getActiveCart(userId);
        cart.getItems().clear();
        cart.setTotalPrice(0.0);

        // Persist & cache
        cartRepository.save(cart);
        redisTemplate.opsForValue().set(redisKey(userId), cart, CART_TTL);
    }

    public void handleOrderConfirmed(Long userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE);
        if (cartOptional.isEmpty()) {
            return; // idempotent handling
        }

        Cart cart = cartOptional.get();

        cart.setStatus(CartStatus.CHECKED_OUT);
        //cart.getItems().clear();
        cartRepository.save(cart);
        redisTemplate.delete(redisKey(userId));
    }
    /* ================= helper ================= */

    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }

}
