package com.example.usermanagement.services;

import com.example.usermanagement.clients.KafkaClient;
import com.example.usermanagement.events.PasswordResetEvent;
import com.example.usermanagement.events.UserCreatedEvent;
import com.example.usermanagement.exceptions.PasswordMismatchException;
import com.example.usermanagement.exceptions.UserAlreadySignedUpException;
import com.example.usermanagement.exceptions.UserNotFoundException;
import com.example.usermanagement.models.User;
import com.example.usermanagement.repos.SessionRepo;
import com.example.usermanagement.repos.UserRepo;
import com.example.usermanagement.security.RsaKeyProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements IAuthService{

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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;


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
            UserCreatedEvent userCreatedEvent = new UserCreatedEvent();
            userCreatedEvent.setEmail(email);
            userCreatedEvent.setName(firstName + " " + lastName);
            kafkaClient.sendMessage("user.created", objectMapper.writeValueAsString(userCreatedEvent));
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

    @Override
    public void logout(String token) {

    }

    @Override
    public void forgetPassword(String email) {
        userRepo.findByEmail(email).ifPresent(user -> {
            // Generate a secure random token
            String token = UUID.randomUUID().toString();
            String redisKey = "password-reset:" + token;

            // Store userId in Redis with 10 min TTL
            redisTemplate.opsForValue().set(redisKey, user.getId(), 10, TimeUnit.MINUTES);

            // Create the reset link
            String resetLink = "http://localhost:8086/api/auth/reset-password/confirm?token=" + token;

            try {
                PasswordResetEvent passwordResetEvent = new PasswordResetEvent();
                passwordResetEvent.setEmail(user.getEmail());
                passwordResetEvent.setName(user.getFirstName() + " " + user.getLastName());
                passwordResetEvent.setResetLink(resetLink);

                kafkaClient.sendMessage("user.password-reset", objectMapper.writeValueAsString(passwordResetEvent));
            } catch (JsonProcessingException ex){
                throw new RuntimeException(ex.getMessage());
            }
        });
    }


    @Override
    public void changePassword(String token, String newPassword) {
        String redisKey = "password-reset:" + token;

        Object obj = redisTemplate.opsForValue().get(redisKey);
        if (obj == null) {
            throw new RuntimeException("Invalid or expired password reset token");
        }

        Long userId = (Long) obj;

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepo.save(user);

        // Delete the token from Redis after use
        redisTemplate.delete(redisKey);
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
