package com.lostedin.authenticator.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String refresh_token;

}
