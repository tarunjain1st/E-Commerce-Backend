package com.example.usermanagement.controllers;

import com.example.usermanagement.dtos.UserInternalResponseDto;
import com.example.usermanagement.dtos.UserProfileRequestDto;
import com.example.usermanagement.dtos.UserProfileResponseDto;
import com.example.usermanagement.models.User;
import com.example.usermanagement.models.UserAddress;
import com.example.usermanagement.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.usermanagement.mappers.UserMapper.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    /* ---------- INTERNAL SERVICE ---------- */

    @GetMapping("/user/{userId}")
    public UserInternalResponseDto getUserById(@PathVariable Long userId) {
        return toInternalDto(userService.getUserById(userId));
    }

    /* ---------- CLIENT APIs ---------- */

    @GetMapping
    public UserProfileResponseDto getUserProfile(@RequestHeader("X-User-Id") Long userId) {
        return toProfileDto(userService.getUserProfile(userId));
    }

    @PatchMapping
    public UserProfileResponseDto updateUserProfile(@RequestHeader("X-User-Id") Long userId, @RequestBody UserProfileRequestDto requestDto) {
        UserAddress address = null;

        if (requestDto.getAddress() != null) {
            address = toUserAddress(requestDto.getAddress());
        }

        User updatedUser = userService.updateUserProfile(
                userId,
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getPhoneNumber(),
                requestDto.getImageUrl(),
                address
        );

        return toProfileDto(updatedUser);
    }

    @DeleteMapping
    public boolean deleteUserProfile(@RequestHeader("X-User-Id") Long userId) {
        return userService.deleteUserProfile(userId);
    }
}
