package com.example.usermanagement.controllers;

import com.example.usermanagement.dtos.SignInRequestDto;
import com.example.usermanagement.dtos.SignUpRequestDto;
import com.example.usermanagement.dtos.UserDto;
import com.example.usermanagement.dtos.ValidateTokenRequest;
import com.example.usermanagement.models.User;
import com.example.usermanagement.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto requestDto){
        User user = authService.signup(requestDto.getName(), requestDto.getEmail(), requestDto.getPassword() ,requestDto.getPhoneNumber());
        return from(user);
    }

    @PostMapping("/login")
    public UserDto signIn(@RequestBody SignInRequestDto requestDto){
        User user = authService.login(requestDto.getEmail(), requestDto.getPassword());
        return from(user);
    }

    @PostMapping("/validateToken")
    public Boolean validateToken(@RequestBody ValidateTokenRequest validateTokenRequest){
        return null;
    }

    //TODO: wrapper for Logout & ForgetPassword api

    User from(UserDto userDto){
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setId(user.getId());
        return user;
    }

    UserDto from(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
