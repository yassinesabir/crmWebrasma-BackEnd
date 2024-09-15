package com.crud.RankinDigitalCrud.Service;


import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenValidator {

    private final JwtDecoder jwtDecoder;

    public JwtTokenValidator(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public boolean validateToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            // You can add additional validation here if needed, e.g. roles, expiration, etc.
            return true; // Token is valid
        } catch (Exception e) {
            return false; // Token is invalid
        }
    }
}
