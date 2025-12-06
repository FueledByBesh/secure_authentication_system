package com.lostedin.authenticator.auth_service.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {
    String username;
    String password;
}
