package com.lostedin.authenticator.user_service.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String username;
//    private String email;

    @OneToOne(mappedBy = "user",orphanRemoval = true)
    private UserCredentials credentials;

}
