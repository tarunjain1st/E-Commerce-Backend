package com.example.usermanagement.controllers;

import com.example.usermanagement.dtos.SignInRequestDto;
import com.example.usermanagement.dtos.SignUpRequestDto;
import com.example.usermanagement.dtos.UserDto;
import com.example.usermanagement.dtos.ValidateTokenRequest;
import com.example.usermanagement.exceptions.UnauthorizedException;
import com.example.usermanagement.models.User;
import com.example.usermanagement.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    public ResponseEntity<UserDto> signIn(@RequestBody SignInRequestDto requestDto){
        Pair<User, String> response = authService.login(requestDto.getEmail(), requestDto.getPassword());
        UserDto userDto = from(response.getFirst());
        String token = response.getSecond();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, token);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(userDto);
    }

    @PostMapping("/validateToken")
    public Boolean validateToken(@RequestBody ValidateTokenRequest validateTokenRequest){
        Boolean result = authService.validateToken(validateTokenRequest.getToken(), validateTokenRequest.getUserId());
        if(!result){
            throw new UnauthorizedException("Session expired! please login again....");
        }
        return true;
    }

    //TODO: wrapper for Logout & ForgetPassword api

    UserDto from(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
