package com.example.ordermanagement.utils;

import com.example.ordermanagement.dtos.*;
import com.example.ordermanagement.models.*;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    /* ------------------- Create Order ------------------- */
    public static OrderAddress toOrderAddress(OrderAddressDto dto) {
        if (dto == null) return null;
        OrderAddress address = new OrderAddress();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        return address;
    }

    /* ------------------- Order → OrderResponseDto ------------------- */
    public static OrderResponseDto toOrderResponse(Order order) {
        if (order == null) return null;

        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDeliveryAddress(toOrderAddressDto(order.getDeliveryAddress()));

        List<OrderItemResponseDto> items = order.getItems().stream()
                .map(OrderMapper::toOrderItemResponse)
                .collect(Collectors.toList());
        dto.setItems(items);

        return dto;
    }

    public static OrderItemResponseDto toOrderItemResponse(OrderItem item) {
        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        return dto;
    }

    public static OrderAddressDto toOrderAddressDto(OrderAddress address) {
        if (address == null) return null;
        OrderAddressDto dto = new OrderAddressDto();
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setPhone(address.getPhone());
        return dto;
    }

    /* ------------------- Order → PaymentOrderDto ------------------- */
    public static PaymentOrderDto toPaymentOrderDto(Order order) {
        if (order == null) return null;
        PaymentOrderDto dto = new PaymentOrderDto();
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerName(order.getCustomerName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderId(order.getId());
        List<PaymentOrderItemDto> items = order.getItems().stream().map(item -> {
            PaymentOrderItemDto p = new PaymentOrderItemDto();
            p.setProductName(item.getProductName());
            p.setQuantity(item.getQuantity().longValue());
            p.setUnitAmount(item.getUnitPrice().longValue());
            return p;
        }).collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }

}
