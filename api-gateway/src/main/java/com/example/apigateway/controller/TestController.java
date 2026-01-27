package com.example.apigateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        String email = (String) request.getAttribute("email");

        Object rolesObj = request.getAttribute("roles");
        List<String> rolesList = Collections.emptyList();

        if (rolesObj instanceof List<?> list) {
            rolesList = list.stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .toList();
        }

        String roles = String.join(",", rolesList);

        return "JWT OK! userId=" + userId + ", email=" + email + ", roles=" + roles;
    }
}
