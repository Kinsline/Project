package com.tradeshift.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${tradeshift.jwt.secret}")
    private String secret;

    @Value("${tradeshift.jwt.expiration:3600000}") // Default 1 hour
    private long expiration;

    // ✅ Generate token using email/username as subject
    public String generateToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret));
    }

    // ✅ Extract username (subject) from token
    public String extractUsername(String token) {
        try {
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
            return decoded.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // ✅ Extract expiration date
    public Date extractExpiration(String token) {
        try {
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
            return decoded.getExpiresAt();
        } catch (Exception e) {
            return null;
        }
    }

    // ✅ Validate if token is valid & belongs to correct user
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username != null &&
                username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token));
    }

    // ✅ Check if token expired
    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration == null || expiration.before(new Date());
    }

    // ✅ Optional: direct subject validation (already used in your JwtFilter)
    public String validateTokenAndGetSubject(String token) {
        DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return decoded.getSubject();
    }
}
