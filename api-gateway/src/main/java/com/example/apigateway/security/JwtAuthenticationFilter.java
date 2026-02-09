//package com.example.apigateway.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.SignatureException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.security.PublicKey;
//import java.util.Collections;
//import java.util.List;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final PublicKey publicKey;
//
//    // Constructor injection ensures singleton and avoids issues with @Autowired in filters
//    public JwtAuthenticationFilter(PublicKey publicKey) {
//        this.publicKey = publicKey;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        // If no Authorization header or not Bearer, reject
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Missing or invalid Authorization header");
//            return;
//        }
//
//        String token = authHeader.substring(7).trim();
//
//        try {
//            // Parse JWT claims safely with public key
//            Claims claims = Jwts.parser()
//                    .verifyWith(publicKey)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            System.out.println("JWT claims: " + claims);
//
//            // Extract fields safely
//            String userId = claims.getSubject(); // standard "sub" claim
//            String email = claims.get("email", String.class);
//
//            // Extract roles safely
//            Object rolesObj = claims.get("role");
//            List<SimpleGrantedAuthority> authorities = Collections.emptyList();
//            if (rolesObj instanceof List<?> rolesList) {
//                authorities = rolesList.stream()
//                        .filter(item -> item instanceof String)
//                        .map(item -> (String) item)
//                        .map(SimpleGrantedAuthority::new)
//                        .toList();
//            }
//
//            // Set authentication in SecurityContext
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            // Optionally set request attributes
//            request.setAttribute("userId", userId);
//            request.setAttribute("email", email);
//            request.setAttribute("roles", rolesObj);
//
//            // Continue filter chain
//            filterChain.doFilter(request, response);
//
//        } catch (SignatureException ex) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid JWT signature");
//        } catch (Exception ex) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid JWT token: " + ex.getMessage());
//        }
//    }
//}
