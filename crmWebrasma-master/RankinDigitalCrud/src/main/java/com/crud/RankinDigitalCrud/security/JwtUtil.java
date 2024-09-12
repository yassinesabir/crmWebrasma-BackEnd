package com.crud.RankinDigitalCrud.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

    private final JwtDecoder jwtDecoder;

    public JwtUtil(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    // Validate the token by decoding it
    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true; // If the token is decoded successfully, it's valid
        } catch (JwtException e) {
            return false; // Token is invalid
        }
    }

    // Extract username from JWT token
    public String extractUsername(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaimAsString("sub"); // "sub" typically represents the user ID in JWT
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token");
        }
    }

    // Extract full name from JWT token
    public String extractFullName(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaimAsString("name"); // "name" contains the user's full name
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token");
        }
    }

    // Extract username from HttpServletRequest
    public String extractUsernameFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null && !token.isEmpty()) {
            return extractUsername(token); // Use the extractUsername method
        }
        throw new RuntimeException("JWT token not found in request");
    }

    // Extract full name from HttpServletRequest
    public String extractFullNameFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null && !token.isEmpty()) {
            return extractFullName(token); // Use the extractFullName method
        }
        throw new RuntimeException("JWT token not found in request");
    }

    // Extract the Bearer token from the Authorization header
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
