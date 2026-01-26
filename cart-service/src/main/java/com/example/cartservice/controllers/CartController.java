package com.example.cartservice.controllers;

import com.example.cartservice.models.Cart;
import com.example.cartservice.services.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    //Add your APIs here
    @GetMapping("/{cartId}")
    public Cart getCartById(@PathVariable Long cartId){
        return cartService.getCartById(cartId);
    }

    @GetMapping("/user/{userId}")
    public List<Cart> getCartByUserId(@PathVariable Long userId){
        return cartService.getCartByUserId(userId);
    }

    @DeleteMapping("/{cartId}")
    public Cart deleteCart(@PathVariable Long cartId){
        return cartService.deleteCartById(cartId);
    }

    @PostMapping
    public Cart createCart(@RequestBody Cart cart){
        return cartService.addCart(cart);
    }

    @PutMapping("/{cartId}")
    public Cart updateCart(@PathVariable Long cartId, @RequestBody Cart cart){
        return cartService.updateCart(cartId, cart);
    }
}

