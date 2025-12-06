package com.lostedin.authenticator.user_service.controller;

import com.lostedin.authenticator.user_service.dto.ResponseDto;
import com.lostedin.authenticator.user_service.exception.InvalidTokenException;
import com.lostedin.authenticator.user_service.model.token.TokenValidator;
import com.lostedin.authenticator.user_service.service.UserSettingsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user/settings")
@RequiredArgsConstructor
public class UserSettignsController {
    private final TokenValidator tokenValidator;
    private final UserSettingsService userSettingsService;

    @GetMapping("/enable-2fa")
    protected ResponseEntity<@NonNull ResponseDto> enable_2fa(
            @CookieValue("access-token") String accessToken
    ){
        UUID userId;
        try {
            userId = getIdFromToken(accessToken);
        }catch (InvalidTokenException e){
            return ResponseEntity.status(403).body(ResponseDto.builder().status(403).message("Invalid access token").build());
        }

        userSettingsService.enable_2fa(userId);
        return ResponseEntity.ok().body(ResponseDto.builder().status(200).message("2FA enabled").build());
    }

    @GetMapping("/disable-2fa")
    protected ResponseEntity<@NonNull ResponseDto> disable_2fa(
            @CookieValue("access-token") String accessToken
    ){
        UUID userId;
        try {
            userId = getIdFromToken(accessToken);
        }catch (InvalidTokenException e){
            return ResponseEntity.status(403).body(ResponseDto.builder().status(403).message("Invalid access token").build());
        }
        userSettingsService.disable_2fa(userId);
        return ResponseEntity.ok().body(ResponseDto.builder().status(200).message("2FA disabled").build());
    }


    private UUID getIdFromToken(String accessToken) throws InvalidTokenException {
        Optional<UUID> optionalUserId = tokenValidator.validateAccessToken(accessToken);
        if(optionalUserId.isEmpty())
            throw new InvalidTokenException("Internal Server Error: Invalid access token");
        return optionalUserId.get();
    }
}
