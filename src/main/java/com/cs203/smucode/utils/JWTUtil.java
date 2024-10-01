package com.cs203.smucode.utils;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    private JwtDecoder jwtDecoder;

    @PostConstruct
    public void init(@Value("${jwt.public.key}") String publicKeyString)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(
            new X509EncodedKeySpec(publicKeyBytes)
        );
        jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }

    public Map<String, Object> getClaimsFromToken(String token) {
        return jwtDecoder.decode(token).getClaims();
    }
}
