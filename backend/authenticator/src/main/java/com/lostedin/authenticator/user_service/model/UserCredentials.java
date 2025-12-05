package com.lostedin.authenticator.user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
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
