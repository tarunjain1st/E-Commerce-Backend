package com.example.usermanagement.controllers;

import com.example.usermanagement.dtos.SignUpResponseDto;
import com.example.usermanagement.models.User;
import com.example.usermanagement.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping("/{id}")
    public SignUpResponseDto getUser(@PathVariable Long id){
        User user = userServiceImpl.getUser(id);
        return from(user);
    }

    private SignUpResponseDto from(User user){
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();
        signUpResponseDto.setId(user.getId());
        signUpResponseDto.setName(user.getFirstName() + " " + user.getLastName());
        signUpResponseDto.setEmail(user.getEmail());
        return signUpResponseDto;
    }
}
