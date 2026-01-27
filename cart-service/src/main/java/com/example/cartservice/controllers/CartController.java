package com.example.cartservice.controllers;

import com.example.cartservice.dtos.*;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItemData;
import com.example.cartservice.services.ICartService;
import com.example.cartservice.utils.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    @GetMapping
    public CartResponseDto getCartByUserId(@PathVariable Long userId) {
        return CartMapper.toCartResponse(cartService.getCartByUserId(userId));
    }

    @PostMapping("/items")
    public CartResponseDto addItem(@PathVariable Long userId,
                                   @RequestBody CartRequestDto dto) {
        CartItemData itemData = CartMapper.toCartItemData(dto);
        Cart cart = cartService.addItemToCart(userId, itemData);
        return CartMapper.toCartResponse(cart);
    }

    @PutMapping("/items")
    public CartResponseDto updateItem(@PathVariable Long userId,
                                      @RequestBody CartRequestDto dto) {
        CartItemData itemData = CartMapper.toCartItemData(dto);
        Cart cart = cartService.updateItemInCart(userId, itemData);
        return CartMapper.toCartResponse(cart);
    }

    @DeleteMapping("/items/{productId}")
    public CartResponseDto removeItem(@PathVariable Long userId,
                                      @PathVariable Long productId) {
        Cart cart = cartService.removeItemFromCart(userId, productId);
        return CartMapper.toCartResponse(cart);
    }

    @DeleteMapping("/clear")
    public void clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
    }
}
