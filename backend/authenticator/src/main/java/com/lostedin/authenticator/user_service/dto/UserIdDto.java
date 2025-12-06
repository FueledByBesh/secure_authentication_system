package com.lostedin.authenticator.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserIdDto {
    UUID id;
}
