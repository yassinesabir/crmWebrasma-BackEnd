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

    public String extractUsernameFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null && !token.isEmpty()) {
            try {
                Jwt jwt = jwtDecoder.decode(token);
                return jwt.getClaimAsString("sub"); // "sub" typically represents the user ID in JWTs
            } catch (JwtException e) {
                throw new RuntimeException("Invalid JWT token");
            }
        }
        throw new RuntimeException("JWT token not found in request");
    }

    public String extractFullNameFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null && !token.isEmpty()) {
            try {
                Jwt jwt = jwtDecoder.decode(token);
                return jwt.getClaimAsString("name"); // "name" contains the user's full name
            } catch (JwtException e) {
                throw new RuntimeException("Invalid JWT token");
            }
        }
        throw new RuntimeException("JWT token not found in request");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
