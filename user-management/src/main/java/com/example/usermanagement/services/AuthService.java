package com.example.usermanagement.services;

import com.example.usermanagement.clients.KafkaClient;
import com.example.usermanagement.dtos.EmailDto;
import com.example.usermanagement.exceptions.PasswordMismatchException;
import com.example.usermanagement.exceptions.UserAlreadySignedUpException;
import com.example.usermanagement.exceptions.UserNotFoundException;
import com.example.usermanagement.models.Status;
import com.example.usermanagement.models.User;
import com.example.usermanagement.models.UserSession;
import com.example.usermanagement.repos.SessionRepo;
import com.example.usermanagement.repos.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SessionRepo  sessionRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SecretKey secretKey;
    @Autowired
    private KafkaClient kafkaClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public User signup(String name, String email, String password, String phoneNumber) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isPresent()){
            throw new UserAlreadySignedUpException("Please login directly....");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        //user.setPassword(password);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);

        try {
            EmailDto emailDto = new EmailDto();
            emailDto.setTo(email);
            emailDto.setFrom("tarun8work@gmail.com");
            emailDto.setSubject("Welcome to Ecommerce app");
            emailDto.setBody("Have a good experience");
            kafkaClient.sendMessage("signup", objectMapper.writeValueAsString(emailDto));
        }catch (JsonProcessingException ex){
            throw new RuntimeException(ex.getMessage());
        }

        return userRepo.save(user);
    }

    @Override
    public Pair<User, String> login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new UserNotFoundException("Please Signup first....");
        }

        User user = userOptional.get();

        //if(!user.getPassword().equals(password)){

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new PasswordMismatchException("Please use valid password....");
        }

        // token generation
//        String message = "{\n" +
//                " \"email\": \"tarun@gmail.com\",\n" +
//                " \"roles\": [\n" +
//                "   \"learner\"\n" +
//                "   \"ta\"\n" +
//                " ],\n" +
//                " \"expiration\": \"2ndJan2026\"\n" +
//                "}";
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("iss", "Scaler");
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("gen", System.currentTimeMillis());
        claims.put("exp", System.currentTimeMillis() + 100000);
        claims.put("access", user.getRoles());

//        MacAlgorithm algorithm = Jwts.SIG.HS256;
//        SecretKey secretKey = algorithm.key().build();
//        String token = Jwts.builder().content(content).compact();
        String token = Jwts.builder().claims(claims).signWith(secretKey).compact();

        UserSession userSession = new UserSession();
        userSession.setToken(token);
        userSession.setUser(user);
        userSession.setStatus(Status.ACTIVE);
        sessionRepo.save(userSession);

        return Pair.of(user, token);
    }

    @Override
    public Boolean validateToken(String token, Long userId) {
        Optional<UserSession> userSessionOptional = sessionRepo.findByTokenAndUser_Id(token, userId);
        System.out.println(userSessionOptional.isPresent());
        if (userSessionOptional.isEmpty()){
            return false;
        }
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseClaimsJws(token).getPayload();
        Long tokenExpiry = (Long) claims.get("exp");
        Long currentTime = System.currentTimeMillis();

        System.out.println("token expiry: " + tokenExpiry);
        System.out.println("currentTime: " + currentTime);

        if (currentTime > tokenExpiry){
            UserSession userSession = userSessionOptional.get();
            userSession.setStatus(Status.INACTIVE);
            sessionRepo.save(userSession);
            return false;
        }
        return true;
    }
}
