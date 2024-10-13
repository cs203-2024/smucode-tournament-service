package com.cs203.smucode.config;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author : gav
 * @version : 1.0
 * @since : 2024-09-04
 *
 * This class is used to configure the security of the tournament microservice.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {
        http.authorizeHttpRequests(
                // need change this
            auth -> auth.anyRequest().permitAll() // For tournament, any request must be authenticated
        );

        http.sessionManagement(
            session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No session management; microservice
        );

        http.csrf(AbstractHttpConfigurer::disable); // Disable CSRF protection since we are using JWT
        http.cors(Customizer.withDefaults());

        http.oauth2ResourceServer(oauth2 ->
            oauth2.jwt(Customizer.withDefaults())
        );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder(
        @Value("${jwt.public.key}") String publicKeyString
    ) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(
            new X509EncodedKeySpec(publicKeyBytes)
        );
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }
}
