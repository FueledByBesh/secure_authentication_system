package com.lostedin.authenticator.auth_service.service;

import com.lostedin.authenticator.auth_service.dto.TokenDto;
import com.lostedin.authenticator.auth_service.entity.Session;
import com.lostedin.authenticator.auth_service.model.token.JwtUtil;
import com.lostedin.authenticator.auth_service.repo.SessionRepo;
import com.lostedin.authenticator.auth_service.util.Hasher;
import com.lostedin.authenticator.auth_service.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepo sessionRepo;
    private final JwtUtil jwtUtil;

    public TokenDto createSession(UUID userId){
        log.info("Starting create session for user: {}", userId);
        UUID sessionId = Helper.generateRandomUUID();
        if (sessionRepo.existsById(sessionId)) return createSession(userId);
        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(sessionId);
        String hasedRefreshToken = Hasher.bcrypt(refreshToken);

        Session session = Session.builder()
                .id(sessionId)
                .userId(userId)
                .refresh_token(hasedRefreshToken)
                .build();
        try {
            sessionRepo.save(session);
        }catch (RuntimeException e){
            log.error("Failed to save session", e);
            return TokenDto.builder().status(500).message("Internal server error").build();
        }
        return TokenDto.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .status(201)
                .message("Session created")
                .build();
    }

    public TokenDto refreshSession(String refreshToken){
        log.info("Refreshing session for refresh token: {}", refreshToken);

        Optional<UUID> optionalSessionId = jwtUtil.validateRefreshToken(refreshToken);
        if(optionalSessionId.isEmpty())
            return TokenDto.builder().status(401).message("Unauthorized").build();
        UUID sessionId = optionalSessionId.get();
        Optional<Session> optionalSession = sessionRepo.findById(sessionId);
        if(optionalSession.isEmpty())
            return TokenDto.builder().status(401).message("Unauthorized").build();

        Session session = optionalSession.get();
        if(!Hasher.verify(refreshToken, session.getRefresh_token()))
            return TokenDto.builder().status(401).message("Unauthorized").build();
        sessionRepo.deleteById(sessionId);
        return createSession(session.getUserId());
    }



}
