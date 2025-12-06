package com.lostedin.authenticator.auth_service.api;

import com.lostedin.authenticator.auth_service.dto.ApiMessageDto;
import com.lostedin.authenticator.auth_service.dto.UserIdWithResponseDto;
import com.lostedin.authenticator.auth_service.dto.user.UserDataDto;
import com.lostedin.authenticator.auth_service.exception.InternalAuthServiceError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAPI {

    private final OkHttpClient client;
    private final String baseUrl = "http://localhost:8080/user";
    public static final MediaType JSON = MediaType.get("application/json");
    private final ObjectMapper objectMapper;

    public Optional<UserDataDto> getUser(UUID id) {
        Request request = new Request.Builder()
                .url(baseUrl + "/me")
                .post(RequestBody.create(objectMapper.writeValueAsString(id), JSON))
                .build();

        try(Response response = client.newCall(request).execute()){
            UserDataDto user;
            String body = response.body().string();
            if (response.isSuccessful()) {
                user = objectMapper.readValue(body, UserDataDto.class);
            } else {
                throw new InternalAuthServiceError(objectMapper.readValue(body, ApiMessageDto.class).getMessage());
            }
            return Optional.of(user);
        }catch (IOException e){
            throw new InternalAuthServiceError(e.getMessage());
        }
    }

    public UserIdWithResponseDto validate(String username, String password) {

        log.debug("Validating user {} with password {}", username, password);

        UserDataDto user = UserDataDto.builder().username(username).password(password).build();
        Request request = new Request.Builder()
                .url(baseUrl + "/validate")
                .post(RequestBody.create(objectMapper.writeValueAsString(user),JSON))
                .build();

        try(Response response = client.newCall(request).execute()){
            String body = response.body().string();
            log.info(body);
            return objectMapper.readValue(body, UserIdWithResponseDto.class);
        }catch (IOException e){
            throw new InternalAuthServiceError(e.getMessage());
        }

    }

    public UserIdWithResponseDto createUser(String username, String password) {
        UserDataDto user = UserDataDto.builder().username(username).password(password).build();
        Request request = new Request.Builder()
                .url(baseUrl + "/create")
                .post(RequestBody.create(objectMapper.writeValueAsString(user),JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            return objectMapper.readValue(body, UserIdWithResponseDto.class);
        } catch (IOException e){
            throw new InternalAuthServiceError(e.getMessage());
        }
    }

}
