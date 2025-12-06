package com.lostedin.authenticator.user_service.controller;


import com.lostedin.authenticator.user_service.dto.ResponseDto;
import com.lostedin.authenticator.user_service.exception.InvalidTokenException;
import com.lostedin.authenticator.user_service.model.token.TokenValidator;
import com.lostedin.authenticator.user_service.service.TotpService;
import com.lostedin.authenticator.user_service.util.QrUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/totp")
@RequiredArgsConstructor
public class TOTPController {

    private final TokenValidator tokenValidator;
    private final TotpService totpService;

    @GetMapping("verify-code")
    protected ResponseEntity<@NonNull ResponseDto> verify_code(
            @RequestParam(value = "code") String code,
            @CookieValue(value = "access-token") String accessToken
    ){
        UUID userId;
        try {
            userId = getIdFromToken(accessToken);
        }catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDto.builder().status(403).message("Invalid access token").build());
        }

        boolean verified = totpService.verifyCode(code,userId);

        if(verified){
            return ResponseEntity.ok().body(ResponseDto.builder().status(200).message("Code verified").build());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDto.builder().status(401).message("Invalid code").build());
    }

    @GetMapping(value = "/totp-qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> totp_qr(
            @CookieValue(value = "access-token") String accessToken
    ) {
        UUID userId = getIdFromToken(accessToken);

        String totp_uri = totpService.getTotpUri(userId);

        byte[] png = QrUtil.generatePng(totp_uri, 256);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    private UUID getIdFromToken(String accessToken) throws InvalidTokenException{
        Optional<UUID> optionalUserId = tokenValidator.validateAccessToken(accessToken);
        if(optionalUserId.isEmpty())
            throw new InvalidTokenException("Internal Server Error: Invalid access token");
        return optionalUserId.get();
    }
}
