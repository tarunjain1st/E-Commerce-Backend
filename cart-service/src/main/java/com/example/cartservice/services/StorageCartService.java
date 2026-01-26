package com.example.cartservice.services;

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

    @Override
    public Cart getCartByUserId(Long userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        if (cartOptional.isPresent()) {
            return cartOptional.get();
        }
        return null;
    }

    @Override
    public Cart addItemToCart(Long userId, CartItemData item) {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(item.getProductId());
        cartItem.setQuantity(item.getQuantity());
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(cartItems);
        return cartRepository.insert(cart);
    }

    @Override
    public Cart updateItemInCart(Long userId, CartItemData item) {
        return null;
    }

    @Override
    public Cart removeItemFromCart(Long userId, Long productId) {
        return null;
    }

    @Override
    public void clearCart(Long userId) {

    }
}
