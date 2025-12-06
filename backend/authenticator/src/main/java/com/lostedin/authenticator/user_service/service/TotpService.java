package com.lostedin.authenticator.user_service.service;

import com.lostedin.authenticator.user_service.model.User;
import com.lostedin.authenticator.user_service.model.UserCredentials;
import com.lostedin.authenticator.user_service.model.token.TokenValidator;
import com.lostedin.authenticator.user_service.model.two_fa.TOTP;
import com.lostedin.authenticator.user_service.repo.UserCredentialsRepo;
import com.lostedin.authenticator.user_service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TotpService {

    private final TOTP totp;
    private final TokenValidator tokenValidator;
    private final UserCredentialsRepo credentialsRepo;

    public boolean verifyCode(String code, UUID userId){

        UserCredentials credentials = getCredentials(userId);

        return totp.verifyCode(credentials.getTotp_secret(),code,1);

    }

    public String getTotpUri(UUID userId){

        UserCredentials credentials = getCredentials(userId);
        String secret = credentials.getTotp_secret();
        User user = credentials.getUser();

        return totp.buildOtpAuthUri("Secure-Authorizer",user.getUsername(),secret);
    }


    private UserCredentials getCredentials(UUID userId){
        Optional<UserCredentials> optionalCredentials = credentialsRepo.getByUserId(userId);
        if(optionalCredentials.isEmpty()){
            throw new RuntimeException("Internal Server Error: User not found");
        }
        return optionalCredentials.get();
    }

}
