package com.lostedin.authenticator.auth_service.controller;

import com.lostedin.authenticator.auth_service.dto.ApiMessageDto;
import com.lostedin.authenticator.auth_service.dto.AuthDto;
import com.lostedin.authenticator.auth_service.dto.TokenDto;
import com.lostedin.authenticator.auth_service.dto.UserIdWithResponseDto;
import com.lostedin.authenticator.auth_service.dto.user.UserIdDto;
import com.lostedin.authenticator.auth_service.service.SessionService;
import com.lostedin.authenticator.auth_service.service.AuthorizationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        TokenDto response = sessionService.createSession(userIdDto.getId());
        if(response.getStatus()==201){
            ResponseCookie cookie1 = ResponseCookie.from("access-token", response.getAccess_token())
                    .httpOnly(true)
                    .maxAge(60*15)
                    .path("/").build();
            ResponseCookie cookie2 = ResponseCookie.from("refresh-token",response.getRefresh_token())
                    .httpOnly(true)
                    .maxAge(60*60*24*14)
                    .path("/auth").build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie1.toString())
                    .header(HttpHeaders.SET_COOKIE, cookie2.toString())
                    .body(response);
        }

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/refresh")
    protected ResponseEntity<@NonNull TokenDto> refreshSession(
            @CookieValue("refresh-token") String refreshToken
    ){
        TokenDto response = sessionService.refreshSession(refreshToken);

        if(response.getStatus()==201){
            ResponseCookie cookie1 = ResponseCookie.from("access-token", response.getAccess_token())
                    .httpOnly(true)
                    .maxAge(60*15)
                    .path("/").build();
            ResponseCookie cookie2 = ResponseCookie.from("refresh-token",response.getRefresh_token())
                    .httpOnly(true)
                    .maxAge(60*60*24*14)
                    .path("/auth").build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie1.toString())
                    .header(HttpHeaders.SET_COOKIE, cookie2.toString())
                    .body(response);
        }

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/logout")
    protected ResponseEntity<@NonNull ApiMessageDto> logout(@CookieValue("access-token") String accessToken){
        ApiMessageDto messageDto = sessionService.deleteSession(accessToken);
        ResponseCookie accessCookie = ResponseCookie.from("access-token", "")
                .httpOnly(true)
                .maxAge(0)
                .path("/").build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh-token","")
                .httpOnly(true)
                .maxAge(0)
                .path("/auth").build();
        return ResponseEntity.status(messageDto.getStatus())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(messageDto);
    }

}
