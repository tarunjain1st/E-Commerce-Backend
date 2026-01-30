package com.example.usermanagement.controllers;

import com.example.usermanagement.dtos.*;
import com.example.usermanagement.models.User;
import com.example.usermanagement.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ForgotPasswordRequestDto requestDto) {
        authService.forgetPassword(requestDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset-password/confirm")
    public ResponseEntity<String> resetPasswordResponse(@RequestParam String token) {
        return ResponseEntity.ok("you can use this token to reset your password: " + token);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequestDto requestDto) {
        authService.changePassword(requestDto.getToken(), requestDto.getNewPassword());
        return ResponseEntity.noContent().build();
    }


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
