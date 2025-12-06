package com.lostedin.authenticator.auth_service.controller;

import com.lostedin.authenticator.auth_service.dto.ApiMessageDto;
import com.lostedin.authenticator.auth_service.dto.AuthDto;
import com.lostedin.authenticator.auth_service.dto.UserIdWithResponseDto;
import com.lostedin.authenticator.auth_service.service.AuthorizationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthorizationService authService;

    @PostMapping("/authorize")
    protected ResponseEntity<@NonNull ApiMessageDto> auth(@RequestBody AuthDto authDto){
        UserIdWithResponseDto response = authService.authorizeUser(authDto);
        // create Token
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/register")
    protected ResponseEntity<@NonNull ApiMessageDto> register(@RequestBody AuthDto authDto){
        ApiMessageDto response = authService.registerUser(authDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
