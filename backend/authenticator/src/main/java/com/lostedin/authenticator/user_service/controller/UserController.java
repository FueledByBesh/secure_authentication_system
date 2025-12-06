package com.lostedin.authenticator.user_service.controller;

import com.lostedin.authenticator.user_service.dto.SignUpDto;
import com.lostedin.authenticator.user_service.dto.ResponseDto;
import com.lostedin.authenticator.user_service.dto.UserDataDto;
import com.lostedin.authenticator.user_service.dto.UserIdDto;
import com.lostedin.authenticator.user_service.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/me")
    protected ResponseEntity<@NonNull ResponseDto> getUser(@RequestBody UserIdDto idDto){
        if (idDto.getId() == null) return ResponseEntity.status(400).body(ResponseDto.builder().status(400).message("User id is required").build());
        ResponseDto response = userService.getUser(idDto.getId());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/validate")
    protected ResponseEntity<@NonNull ResponseDto> validateUser(@RequestBody SignUpDto userDto){
        ResponseDto response = userService.validateUser(userDto.getUsername(), userDto.getPassword());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/create")
    protected ResponseEntity<@NonNull ResponseDto> createUser(@RequestBody SignUpDto userDto){
        ResponseDto response = userService.createUser(userDto);
        return  ResponseEntity.status(response.getStatus()).body(response);
    }



}
