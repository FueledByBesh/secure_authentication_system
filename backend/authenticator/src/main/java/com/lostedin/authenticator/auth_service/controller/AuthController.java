package com.lostedin.authenticator.auth_service.controller;

import com.lostedin.authenticator.auth_service.dto.ApiMessageDto;
import com.lostedin.authenticator.auth_service.dto.AuthDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/")
    protected ResponseEntity<@NonNull ApiMessageDto> auth(AuthDto user){
        return ResponseEntity.status(501).body(
                ApiMessageDto.builder()
                        .status(501)
                        .message("Not implemented")
                        .build()
        );
    }


}
