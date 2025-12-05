package com.lostedin.authenticator.user_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentials {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean two_fa_enabled = false;
    private String totp_secret;

    @OneToOne
    @MapsId
    @JoinColumn
    private User user;

}
