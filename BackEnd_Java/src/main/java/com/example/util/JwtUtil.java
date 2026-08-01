package com.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key secretKey = Keys.hmacShaKeyFor("MySecretKeyForJwt123456789012345".getBytes());

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)              // USER IDENTIFIER
                .claim("role", role)            // ROLE AS CLAIM
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hour
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String generateResetToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "RESET_PASSWORD")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isResetTokenValid(String token) {
        Claims claims = getClaims(token);

        String type = claims.get("type", String.class);

        if (!"RESET_PASSWORD".equals(type)) {
            throw new RuntimeException("Invalid reset token");
        }

        return true;
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }
}
