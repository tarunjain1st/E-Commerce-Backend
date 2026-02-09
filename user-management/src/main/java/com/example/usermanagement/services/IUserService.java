package com.example.usermanagement.services;

import com.example.usermanagement.models.User;
import com.example.usermanagement.models.UserAddress;

import java.util.List;

public interface IUserService {
    User getUserById(Long userId);
    User getUserProfile(Long userId);
    User updateUserProfile(
            Long userId,
            String firstName,
            String lastName,
            String phoneNumber,
            String imageUrl,
            UserAddress address
    );
    boolean deleteUserProfile(Long userId);
}
