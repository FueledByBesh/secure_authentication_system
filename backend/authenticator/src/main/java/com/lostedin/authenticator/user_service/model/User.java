package com.lostedin.authenticator.user_service.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String email;

    @OneToOne
    private UserCredentials credentials;

}
