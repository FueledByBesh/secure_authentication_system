package com.lostedin.authenticator.user_service.controller;

import com.lostedin.authenticator.user_service.dto.ResponseDto;
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
    protected ResponseEntity<@NonNull ResponseDto> getUserInfo(@RequestBody UUID id){
        ResponseDto response = userService.getUser(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/validate")
    protected ResponseEntity<@NonNull ResponseDto> validateUser(@RequestBody String username, @RequestBody String password){
        ResponseDto response = userService.validateUser(username, password);
        return ResponseEntity.status(response.getStatus()).body(response);
    }



}
