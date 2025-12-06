package com.lostedin.authenticator.auth_service.controller;

import com.lostedin.authenticator.auth_service.dto.ApiMessageDto;
import com.lostedin.authenticator.auth_service.dto.AuthDto;
import com.lostedin.authenticator.auth_service.dto.UserIdWithResponseDto;
import com.lostedin.authenticator.auth_service.dto.user.UserIdDto;
import com.lostedin.authenticator.auth_service.service.SessionService;
import com.lostedin.authenticator.auth_service.service.AuthorizationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthorizationService authService;
    private final SessionService sessionService;

    @PostMapping("/authorize")
    protected ResponseEntity<@NonNull ApiMessageDto> auth(@RequestBody AuthDto authDto){
        UserIdWithResponseDto response = authService.authorizeUser(authDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/register")
    protected ResponseEntity<@NonNull ApiMessageDto> register(@RequestBody AuthDto authDto){
        ApiMessageDto response = authService.registerUser(authDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/create-session")
    protected ResponseEntity<@NonNull ApiMessageDto> createSession(@RequestBody UserIdDto userIdDto){
        ApiMessageDto response = sessionService.createSession(userIdDto.getId());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
