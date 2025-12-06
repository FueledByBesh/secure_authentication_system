package com.lostedin.authenticator.user_service.service;


import com.lostedin.authenticator.user_service.dto.SignUpDto;
import com.lostedin.authenticator.user_service.dto.ResponseDto;
import com.lostedin.authenticator.user_service.dto.UserDataDto;
import com.lostedin.authenticator.user_service.model.User;
import com.lostedin.authenticator.user_service.model.UserCredentials;
import com.lostedin.authenticator.user_service.repo.UserCredentialsRepo;
import com.lostedin.authenticator.user_service.repo.UserRepo;
import com.lostedin.authenticator.user_service.util.PasswordEncrypter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final UserCredentialsRepo credentialsRepo;
//    private final EntityManager entityManager;

    public ResponseDto createUser(SignUpDto userDto){
        if (userRepo.getByUsername(userDto.getUsername()).isPresent()) {
            return ResponseDto.builder().status(409).message("Username already taken").build();
        }

        if (userDto.getPassword() == null || userDto.getPassword().length() < 8) {
            return ResponseDto.builder().status(400).message("Password must be at least 8 characters long").build();
        }

        String encryptedPassword = PasswordEncrypter.hash(userDto.getPassword());

        User user = User.builder()
                .username(userDto.getUsername())
                .build();
        user = userRepo.save(user);

        UserCredentials credentials = UserCredentials.builder()
                .user(user)
                .password(encryptedPassword)
                .build();
        credentialsRepo.saveAndFlush(credentials);
        return UserDataDto.builder().status(201).message("User created").id(user.getId()).build();
    }

    public ResponseDto getUser(UUID id){
        Optional<User> optionalUser = userRepo.getByUserId(id);
        if(optionalUser.isEmpty())
            return ResponseDto.builder().status(404).message("User not found").build();

        User user = optionalUser.get();
        UserCredentials credentials = user.getCredentials();
        return UserDataDto.builder()
                .status(200)
                .id(id)
                .username(user.getUsername())
                .password(credentials.getPassword())
                .is_2fa_enabled(credentials.isTwo_fa_enabled())
                .totp_secret(credentials.getTotp_secret())
                .build();
    }

    public ResponseDto validateUser(String username, String password){

        log.debug("Validating user {} with password {}", username, password);
        Optional<User> optionalUser = userRepo.getByUsername(username);
        if (optionalUser.isEmpty())
            return ResponseDto.builder().status(404).message("User not found").build();
        
        User user = optionalUser.get();
        UserCredentials credentials = user.getCredentials();
        if (credentials == null || credentials.getPassword() == null) {
            return ResponseDto.builder().status(500).message("Internal Server Error").build();
        }

        boolean ok = PasswordEncrypter.verify(password, credentials.getPassword());
        return ok ?
                UserDataDto.builder().status(200).id(user.getId()).is_2fa_enabled(credentials.isTwo_fa_enabled()).build() :
                ResponseDto.builder().status(403).message("Wrong credentials").build();
    }

    public ResponseDto get2faSecretIfEnabled(UUID id){
        Optional<UserCredentials> optionalCredentials = credentialsRepo.getByUserId(id);
        if(optionalCredentials.isEmpty())
            return ResponseDto.builder().status(404).message("User not found").build();

        UserCredentials credentials = optionalCredentials.get();
        if (credentials.isTwo_fa_enabled()){
            if(Objects.isNull(credentials.getTotp_secret()) || credentials.getTotp_secret().isEmpty())
                return ResponseDto.builder().status(500).message("Internal Server Error").build();
            return UserDataDto.builder().status(200).is_2fa_enabled(true).totp_secret(credentials.getTotp_secret()).build();
        }
        return UserDataDto.builder().status(200).is_2fa_enabled(false).build();
    }






}
