package com.example.cartservice.services;

import com.example.cartservice.clients.ProductClient;
import com.example.cartservice.dtos.ProductDto;
import com.example.cartservice.exceptions.*;
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

@Service
public class StorageCartService implements ICartService {

    private static final Duration CART_TTL = Duration.ofHours(1);

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
        if (userId == null || userId <= 0) {
            throw new InvalidCartRequestException("Invalid user id");
        }

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String key = redisKey(userId);

        // Check Redis cache first
        Cart cart = (Cart) ops.get(key);
        if (cart != null) return cart;

        // Fallback to DB
        cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setItems(new ArrayList<>());
                    newCart.setTotalPrice(0.0);
                    newCart.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(newCart);
                });

        // Cache in Redis
        ops.set(key, cart, CART_TTL);
        return cart;
    }

    @Override
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidCartRequestException("Quantity must be greater than zero");
        }
        if (productId == null || productId <= 0) {
            throw new InvalidCartRequestException("Invalid product id");
        }

        Cart cart = getActiveCart(userId);

        // Fetch product from Product Service (may throw ProductNotFoundException)
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
        cartRepository.save(cart);
        redisTemplate.opsForValue().set(redisKey(userId), cart, CART_TTL);

        return cart;
    }

    @Override
    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = getActiveCart(userId);

        boolean removed = cart.getItems().removeIf(ci -> ci.getProductId().equals(productId));
        if (!removed) {
            throw new CartItemNotFoundException(productId);
        }

        recalculateTotal(cart);
        cartRepository.save(cart);
        redisTemplate.opsForValue().set(redisKey(userId), cart, CART_TTL);

        return cart;
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getActiveCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException(userId);
        }

        cart.getItems().clear();
        cart.setTotalPrice(0.0);

        cartRepository.save(cart);
        redisTemplate.opsForValue().set(redisKey(userId), cart, CART_TTL);
    }

    public void handleOrderConfirmed(Long userId) {
        cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .ifPresent(cart -> {
                    cart.setStatus(CartStatus.CHECKED_OUT);
                    cartRepository.save(cart);
                    redisTemplate.delete(redisKey(userId));
                });
    }

    /* ---------- Helper ---------- */
    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }
}
