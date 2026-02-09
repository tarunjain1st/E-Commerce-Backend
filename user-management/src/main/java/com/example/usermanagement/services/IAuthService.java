package com.example.usermanagement.services;

import com.example.usermanagement.models.User;
import org.springframework.data.util.Pair;

public interface IAuthService {
    User signup(String firstName, String lastName,String email, String password, String phoneNumber);
    Pair<Long, String> login(String email, String password);
    void logout(String token);
    void forgetPassword(String email);
    void changePassword(String token, String newPassword);
}
