package com.lostedin.authenticator.auth_service.repo;

import com.lostedin.authenticator.auth_service.entity.Session;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessionRepo extends JpaRepository<@NonNull Session, @NonNull UUID> {

}
