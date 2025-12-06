package com.lostedin.authenticator.auth_service.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto extends ApiMessageDto{
    String access_token;
    String refresh_token;
}
