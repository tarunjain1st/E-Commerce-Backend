package com.example.cartservice.controllers;

import com.example.cartservice.dtos.*;
import com.example.cartservice.models.Cart;
import com.example.cartservice.services.ICartService;
import com.example.cartservice.utils.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/users/{userId}/cart")
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private ICartService cartService;

    @GetMapping
    public CartResponseDto getCartByUserId(@RequestHeader("X-User-Id") Long userId) {
        return CartMapper.toCartResponse(cartService.getActiveCart(userId));
    }

    @PostMapping("/items")
    public CartResponseDto addItem(@RequestHeader("X-User-Id") Long userId, @RequestBody CartRequestDto dto) {
        Cart cart = cartService.addItemToCart(userId, dto.getProductId(), dto.getQuantity());
        return CartMapper.toCartResponse(cart);
    }

    @DeleteMapping("/items/{productId}")
    public CartResponseDto removeItem(@RequestHeader("X-User-Id") Long userId, @PathVariable Long productId) {
        Cart cart = cartService.removeItemFromCart(userId, productId);
        return CartMapper.toCartResponse(cart);
    }

    @DeleteMapping("/clear")
    public void clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
    }

}
