package com.example.cartservice.services;

import com.example.cartservice.clients.ProductClient;
import com.example.cartservice.dtos.ProductDto;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;
import com.example.cartservice.repos.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;

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
    public Cart getCartByUserId(Long userId) {

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String key = redisKey(userId);

        // Check Redis cache first
        Cart cart = (Cart) ops.get(key);
        if (cart != null) return cart;

        // Fallback to MongoDB
        cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setItems(new ArrayList<>());
                    newCart.setTotalPrice(0.0);
                    return cartRepository.save(newCart);
                });

        // Cache in Redis with TTL
        ops.set(key, cart, CART_TTL);

        return cart;
    }

    @Override
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {

        Cart cart = getCartByUserId(userId);

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

        Cart cart = getCartByUserId(userId);

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

        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cart.setTotalPrice(0.0);

        // Persist & cache
        cartRepository.save(cart);
        redisTemplate.opsForValue().set(redisKey(userId), cart, CART_TTL);
    }

    @Override
    public Cart checkoutCart(Long userId) {
        Cart cart = getCartByUserId(userId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout an empty cart");
        }

        // Create a snapshot (read-only) to return
        Cart checkoutSnapshot = new Cart();
        checkoutSnapshot.setId(cart.getId());
        checkoutSnapshot.setUserId(cart.getUserId());
        checkoutSnapshot.setItems(new ArrayList<>(cart.getItems()));
        checkoutSnapshot.setTotalPrice(cart.getTotalPrice());

        // Clear the original cart for the user
        clearCart(userId);

        return checkoutSnapshot;
    }

    /* ================= helper ================= */

    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }
}
