package com.example.usermanagement.controllers;

import com.example.usermanagement.dtos.UserInternalResponseDto;
import com.example.usermanagement.dtos.UserProfileRequestDto;
import com.example.usermanagement.dtos.UserProfileResponseDto;
import com.example.usermanagement.models.User;
import com.example.usermanagement.models.UserAddress;
import com.example.usermanagement.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.usermanagement.mappers.UserMapper.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    /* ---------- INTERNAL SERVICE ---------- */

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserInternalResponseDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(toInternalDto(userService.getUserById(userId)));
    }

    /* ---------- CLIENT APIs ---------- */

    @GetMapping
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(toProfileDto(userService.getUserProfile(userId)));
    }

    @PatchMapping
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserProfileRequestDto requestDto) {

        UserAddress address = requestDto.getAddress() != null ? toUserAddress(requestDto.getAddress()) : null;

        User updatedUser = userService.updateUserProfile(
                userId,
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getPhoneNumber(),
                requestDto.getImageUrl(),
                address
        );

        return ResponseEntity.ok(toProfileDto(updatedUser));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserProfile(@RequestHeader("X-User-Id") Long userId) {
        userService.deleteUserProfile(userId);
        return ResponseEntity.noContent().build();
    }

}
