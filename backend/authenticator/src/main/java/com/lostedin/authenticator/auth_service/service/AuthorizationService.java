package com.lostedin.authenticator.auth_service.service;

import com.lostedin.authenticator.auth_service.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    public boolean authorizeUser(AuthDto user){

        return false;
    }




}
