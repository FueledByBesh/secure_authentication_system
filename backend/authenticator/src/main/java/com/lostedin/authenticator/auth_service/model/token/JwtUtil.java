package com.lostedin.authenticator.auth_service.model.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwtUtil {

    private final SecretKey key;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-ttl-seconds:900}") long accessTtlSeconds,         // default 15 minutes
            @Value("${jwt.refresh-ttl-seconds:1209600}") long refreshTtlSeconds    // default 14 days
    ) {
        // Use base64 if provided; otherwise bytes directly. Ensure 256-bit minimum for HS256
        SecretKey tmp;
        try {
            byte[] bytes = Decoders.BASE64.decode(secret);
            tmp = Keys.hmacShaKeyFor(bytes);
        } catch (IllegalArgumentException e) {
            byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
            tmp = Keys.hmacShaKeyFor(bytes);
        }
        this.key = tmp;
        this.accessTtl = Duration.ofSeconds(accessTtlSeconds);
        this.refreshTtl = Duration.ofSeconds(refreshTtlSeconds);
    }

    public String generateAccessToken(UUID userId) {
        return buildToken(userId.toString(), accessTtl);
    }

    public String generateRefreshToken(UUID sessionId) {
        return buildToken(sessionId.toString(), refreshTtl);
    }

    public Optional<UUID> validateAccessToken(String token) {
        return parseAndValidate(token);
    }

    public Optional<UUID> validateRefreshToken(String token) {
        return parseAndValidate(token);
    }

    private String buildToken(String subject, Duration ttl) {
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key) // algorithm derived from key (HS256 for HMAC-SHA-256 key length)
                .compact();
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
