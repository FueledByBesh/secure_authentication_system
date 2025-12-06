package com.lostedin.authenticator.auth_service.service;

import com.lostedin.authenticator.auth_service.api.UserAPI;
import com.lostedin.authenticator.auth_service.dto.ApiMessageDto;
import com.lostedin.authenticator.auth_service.dto.AuthDto;
import com.lostedin.authenticator.auth_service.dto.UserIdWithResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserAPI userAPI;
    private final SessionService sessionService;

    public UserIdWithResponseDto authorizeUser(AuthDto user){
        return userAPI.validate(user.getUsername(), user.getPassword());
    }

    public ApiMessageDto registerUser(AuthDto authDto){
        return userAPI.createUser(authDto.getUsername(), authDto.getPassword());
    }


}
