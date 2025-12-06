package com.lostedin.authenticator.user_service.service;

import com.lostedin.authenticator.user_service.dto.UserSettingsDto;
import com.lostedin.authenticator.user_service.model.User;
import com.lostedin.authenticator.user_service.model.UserCredentials;
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
public class UserSettingsService {

    private final UserRepo userRepo;
    private final UserCredentialsRepo credentialsRepo;
    private final TOTP totp;

    public UserSettingsDto getUserSettings(UUID userId){
        Optional<User> optionalUser = userRepo.getByUserId(userId);
        if(optionalUser.isEmpty()){
            throw new RuntimeException("Internal Server Error: User not found");
        }
        User user = optionalUser.get();
        UserCredentials credentials = user.getCredentials();
        return UserSettingsDto.builder()
                .username(user.getUsername())
                .password(credentials.getPassword())
                .is_2fa_enabled(credentials.isTwo_fa_enabled())
                .totp_secret(credentials.getTotp_secret())
                .build();
    }

    public String enable_2fa(UUID userId){
        Optional<UserCredentials> credentials = credentialsRepo.getByUserId(userId);
        if(credentials.isEmpty()){
            throw new RuntimeException("Internal Server Error: User not found");
        }
        UserCredentials userCredentials = credentials.get();
        String secret = totp.generateSecret(20);
        userCredentials.setTwo_fa_enabled(true);
        userCredentials.setTotp_secret(secret);
        credentialsRepo.saveAndFlush(userCredentials);
        return secret;
    }

    public void disable_2fa(UUID userId){
        Optional<UserCredentials> credentials = credentialsRepo.getByUserId(userId);
        if(credentials.isEmpty()){
            throw new RuntimeException("Internal Server Error: User not found");
        }
        UserCredentials userCredentials = credentials.get();
        userCredentials.setTwo_fa_enabled(false);
        userCredentials.setTotp_secret(null);
        credentialsRepo.saveAndFlush(userCredentials);
    }
}
