package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class Jwt {

    // 256-bit secret key required for HS256
    private final Key key = Keys.hmacShaKeyFor(
            "MySuperSecurityKeyforJwtAuthentication12345".getBytes()
    );

    private final long EXPIRE_TIME = 1000L * 60 * 60 * 6; // 6 hours

    // Generate JWT token
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(key) // <---- FIXED (correct JJWT v0.11+ usage)
                .compact();
    }

    // Extract email
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Extract role
    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    // Extract claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validate JWT
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
