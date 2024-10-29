package com.cs203.smucode.utils;

import com.cs203.smucode.exceptions.InvalidTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

/**
 * @author gav
 * @version 1.0
 * @since 2024-10-28
 *
 * This class is used to abstract user information from JWT.
 */

public class JWTUtil {

    private JWTUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getClaim(Authentication authentication, String key) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication cannot be null");
        }

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            throw new InvalidTokenException("Invalid token type, expected JWT");
        }

        Map<String, Object> jwtClaims = jwtAuthenticationToken.getTokenAttributes();
        return jwtClaims.get(key).toString();
    }
}