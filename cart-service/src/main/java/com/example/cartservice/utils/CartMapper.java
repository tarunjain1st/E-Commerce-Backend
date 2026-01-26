package com.example.cartservice.utils;

import com.example.cartservice.dtos.CartItemRequestDto;
import com.example.cartservice.dtos.CartItemResponseDto;
import com.example.cartservice.dtos.CartResponseDto;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;
import com.example.cartservice.models.CartItemData;

import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    // --- Request DTO → Service Layer Data ---
    public static CartItemData toCartItemData(CartItemRequestDto dto) {
        return new CartItemData(dto.getProductId(), dto.getQuantity());
    }

    public static List<CartItemData> toCartItemDataList(List<CartItemRequestDto> dtos) {
        return dtos.stream().map(CartMapper::toCartItemData).collect(Collectors.toList());
    }

    // --- Entity → Response DTO ---
    public static CartItemResponseDto toCartItemResponse(CartItem item) {
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    public static List<CartItemResponseDto> toCartItemResponseList(List<CartItem> items) {
        return items.stream().map(CartMapper::toCartItemResponse).collect(Collectors.toList());
    }

    public static CartResponseDto toCartResponse(Cart cart) {
        CartResponseDto dto = new CartResponseDto();
        dto.setCartId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setItems(toCartItemResponseList(cart.getItems()));
        return dto;
    }
}
