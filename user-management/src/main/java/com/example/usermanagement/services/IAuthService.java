package com.example.usermanagement.services;

import com.example.usermanagement.models.User;
import org.springframework.data.util.Pair;

public interface IAuthService {
    User signup(String name, String email, String password, String phoneNumber);
    Pair<User, String> login(String email, String password);
    Boolean validateToken(String token, Long userId);
}
