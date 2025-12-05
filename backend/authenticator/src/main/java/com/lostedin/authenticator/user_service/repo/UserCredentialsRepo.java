package com.lostedin.authenticator.user_service.repo;

import com.lostedin.authenticator.user_service.model.UserCredentials;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCredentialsRepo extends JpaRepository<@NonNull UserCredentials, @NonNull UUID> {

    Optional<UserCredentials> getByUserId(UUID id);

}
