package com.example.cartservice.utils;

import com.example.cartservice.dtos.CartItemDto;
import com.example.cartservice.dtos.CartRequestDto;
import com.example.cartservice.dtos.CartResponseDto;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;

import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    // --- Entity → Response DTO ---
    public static CartItemDto toCartItemResponse(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    public static List<CartItemDto> toCartItemResponseList(List<CartItem> items) {
        return items.stream().map(CartMapper::toCartItemResponse).collect(Collectors.toList());
    }

    public static CartResponseDto toCartResponse(Cart cart) {
        CartResponseDto dto = new CartResponseDto();
        dto.setCartId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setItems(toCartItemResponseList(cart.getItems()));
        dto.setTotalPrice(cart.getTotalPrice());
        return dto;
    }
}
