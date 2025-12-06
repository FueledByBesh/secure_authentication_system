package com.lostedin.authenticator.user_service.repo;

import com.lostedin.authenticator.user_service.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<@NonNull User, @NonNull UUID> {

    Optional<User> getByUsername(String username);
    @Query("select u from User u where u.id = :id")
    Optional<User> getByUserId(UUID id);

}
