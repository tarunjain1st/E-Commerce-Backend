package com.example.usermanagement.controllers;

import com.example.usermanagement.dtos.UserDto;
import com.example.usermanagement.models.User;
import com.example.usermanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserDto  getUser(@PathVariable Long id){
        User user = userService.getUser(id);
        return from(user);
    }

    private UserDto from(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
