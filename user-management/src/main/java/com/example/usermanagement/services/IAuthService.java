package com.example.usermanagement.services;

import com.example.usermanagement.models.User;

public interface IAuthService {
    User signup(String name, String email, String password, String phoneNumber);
    User login(String email, String password);
    Boolean validateToken(String token, Long userId);
}
