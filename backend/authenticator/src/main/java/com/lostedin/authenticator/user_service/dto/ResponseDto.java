package com.lostedin.authenticator.user_service.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ResponseDto {
    int status;
    String message;
}
