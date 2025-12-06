package com.lostedin.authenticator.auth_service.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDataDto {

    UUID id;
    String username;
    String password;
    Boolean is_2fa_enabled;
    String totp_secret;

}
