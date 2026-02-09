package com.example.usermanagement.mappers;

import com.example.usermanagement.dtos.*;
import com.example.usermanagement.models.User;
import com.example.usermanagement.models.UserAddress;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    /* ---------------- USER → DTO ---------------- */

    public static UserProfileResponseDto toProfileDto(User user) {
        if (user == null) return null;

        UserProfileResponseDto dto = new UserProfileResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddresses(mapAddresses(user.getUserAddresses()));
        dto.setImageUrl(user.getImageUrl());
        return dto;
    }

    public static UserInternalResponseDto toInternalDto(User user) {
        if (user == null) return null;

        UserInternalResponseDto dto = new UserInternalResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddresses(mapAddresses(user.getUserAddresses()));
        if (user.getStatus() != null) dto.setStatus(user.getStatus().toString());
        return dto;
    }

    /* ---------------- ADDRESS MAPPING ---------------- */

    public static List<UserAddressResponseDto> mapAddresses(List<UserAddress> addresses) {
        if (addresses == null) return null;

        return addresses.stream().map(UserMapper::toAddressDto).collect(Collectors.toList());
    }

    public static UserAddressResponseDto toAddressDto(UserAddress address) {
        if (address == null) return null;

        UserAddressResponseDto dto = new UserAddressResponseDto();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        dto.setPostalCode(address.getPostalCode());
        dto.setPhone(address.getPhone());
        return dto;
    }

    public static UserAddress toUserAddress(UserAddressRequestDto dto) {
        if (dto == null) return null;

        UserAddress address = new UserAddress();
        address.setId(dto.getId());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        address.setPhone(dto.getPhone());
        return address;
    }
}
