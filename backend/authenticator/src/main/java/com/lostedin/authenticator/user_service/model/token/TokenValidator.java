package com.lostedin.authenticator.user_service.model.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenValidator {
    private final SecretKey key;

    public TokenValidator(
            @Value("${jwt.secret}") String secret
    ) {
        // Use base64 if provided; otherwise bytes directly. Ensure a 256-bit minimum for HS256
        SecretKey tmp;
        try {
            byte[] bytes = Decoders.BASE64.decode(secret);
            tmp = Keys.hmacShaKeyFor(bytes);
        } catch (IllegalArgumentException e) {
            byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
            tmp = Keys.hmacShaKeyFor(bytes);
        }
        this.key = tmp;
    }

    public Optional<UUID> validateAccessToken(String token) {
        return parseAndValidate(token);
    }

    private Optional<UUID> parseAndValidate(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            String sub = claims.getSubject();
            if (sub == null) return Optional.empty();
            return Optional.of(UUID.fromString(sub));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

