package com.lostedin.authenticator.auth_service.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class AuthDto {
    UUID id;
    String password;
}
