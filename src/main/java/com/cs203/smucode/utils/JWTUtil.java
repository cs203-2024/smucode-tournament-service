package com.cs203.smucode.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    private JwtDecoder jwtDecoder;

    @Autowired
    public JWTUtil(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public Map<String, Object> getClaimsFromToken(String token) {
        return jwtDecoder.decode(token).getClaims();
    }
}
