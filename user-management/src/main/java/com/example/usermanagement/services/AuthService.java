package com.example.usermanagement.services;

import com.example.usermanagement.exceptions.PasswordMismatchException;
import com.example.usermanagement.exceptions.UserAlreadySignedUpException;
import com.example.usermanagement.exceptions.UserNotFoundException;
import com.example.usermanagement.models.User;
import com.example.usermanagement.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepo userRepo;

    @Override
    public User signup(String name, String email, String password, String phoneNumber) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isPresent()){
            throw new UserAlreadySignedUpException("Please login directly....");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        return userRepo.save(user);
    }

    @Override
    public User login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new UserNotFoundException("Please Signup first....");
        }
        User user = userOptional.get();
        if(!user.getPassword().equals(password)){
            throw new PasswordMismatchException("Please use valid password....");
        }
        return user;
    }

    @Override
    public Boolean validateToken(String token, Long userId) {
        return null;
    }
}
