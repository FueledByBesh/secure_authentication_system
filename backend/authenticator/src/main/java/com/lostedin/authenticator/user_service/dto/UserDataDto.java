package com.lostedin.authenticator.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDataDto extends ResponseDto{
    UUID id;
    String username;
    String password;
    Boolean is_2fa_enabled;
    String totp_secret;
}
