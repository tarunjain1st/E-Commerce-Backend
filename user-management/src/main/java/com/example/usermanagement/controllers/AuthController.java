package com.example.usermanagement.controllers;

import com.example.usermanagement.dtos.*;
import com.example.usermanagement.models.User;
import com.example.usermanagement.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public SignUpResponseDto signUp(@RequestBody SignUpRequestDto requestDto){
        User user = authService.signup(
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getPhoneNumber()
        );
        return from(user);
    }

    @PostMapping("/login")
    public ResponseEntity<SignInResponseDto> signIn(@RequestBody SignInRequestDto requestDto){
        Pair<Long, String> userInfo = authService.login(requestDto.getEmail(), requestDto.getPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + userInfo.getSecond());
        return ResponseEntity.ok().headers(headers).body(from(userInfo));
    }

    //TODO: wrapper for Logout & ForgetPassword api

    SignUpResponseDto from(User user){
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();
        signUpResponseDto.setId(user.getId());
        signUpResponseDto.setName(user.getFirstName() +  " " + user.getLastName());
        signUpResponseDto.setEmail(user.getEmail());
        return signUpResponseDto;
    }
    SignInResponseDto from(Pair<Long, String> userInfo){
        SignInResponseDto signInResponseDto = new SignInResponseDto();
        signInResponseDto.setUserId(userInfo.getFirst());
        signInResponseDto.setAccessToken(userInfo.getSecond());
        return signInResponseDto;
    }
}
