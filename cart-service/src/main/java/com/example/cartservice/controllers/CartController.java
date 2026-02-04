package com.example.cartservice.controllers;

import com.example.cartservice.dtos.*;
import com.example.cartservice.models.Cart;
import com.example.cartservice.services.ICartService;
import com.example.cartservice.utils.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/users/{userId}/cart")
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private ICartService cartService;

    // -------------------- GET CART --------------------
    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(
            @RequestHeader("X-User-Id") Long userId) {

        Cart cart = cartService.getActiveCart(userId);
        return ResponseEntity.ok(CartMapper.toCartResponse(cart));
    }

    // -------------------- ADD ITEM --------------------
    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CartRequestDto dto) {

        Cart cart = cartService.addItemToCart(
                userId, dto.getProductId(), dto.getQuantity());

        return ResponseEntity.ok(CartMapper.toCartResponse(cart));
    }

    // -------------------- REMOVE ITEM --------------------
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponseDto> removeItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId) {

        Cart cart = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(CartMapper.toCartResponse(cart));
    }

    // -------------------- CLEAR CART --------------------
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-User-Id") Long userId) {

        cartService.clearCart(userId);
        return ResponseEntity.noContent().build(); // 204
    }
}
