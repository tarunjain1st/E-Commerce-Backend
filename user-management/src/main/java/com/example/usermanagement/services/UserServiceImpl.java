package com.example.usermanagement.services;

import com.example.usermanagement.models.User;
import com.example.usermanagement.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {
    @Autowired
    UserRepo userRepo;

    public User getUser(Long id){
        User user = userRepo.findById(id).get();
        return user;
    }
}
