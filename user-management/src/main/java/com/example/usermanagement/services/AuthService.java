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
import com.example.usermanagement.security.RsaKeyProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    private KafkaClient kafkaClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RsaKeyProvider rsaKeyProvider;


    @Override
    public User signup(String firstName, String lastName, String email, String password, String phoneNumber) {

        if(userRepo.findByEmail(email).isPresent()){
            throw new UserAlreadySignedUpException("Please login directly....");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        user = userRepo.save(user);

        try {
            EmailDto emailDto = new EmailDto();
            emailDto.setTo(email);
            emailDto.setFrom("tarun8work@gmail.com");
            emailDto.setSubject("Welcome to Ecommerce app");
            emailDto.setBody("Have a good experience");
            kafkaClient.sendMessage("signup", objectMapper.writeValueAsString(emailDto));
        } catch (JsonProcessingException ex){
            throw new RuntimeException(ex.getMessage());
        }

        return user;
    }

    @Override
    public Pair<Long, String> login(String email, String password) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Please Signup first...."));

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new PasswordMismatchException("Please use valid password....");
        }
        String token = generateAccessToken(user);
        return Pair.of(user.getId(), token);
    }
    private String generateAccessToken(User user){
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("email", user.getEmail());
        claims.put("role",  user.getRoles());

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuer("auth-service")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15*60*1000)) // 15min
                .addClaims(claims)
                .signWith(rsaKeyProvider.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

}
