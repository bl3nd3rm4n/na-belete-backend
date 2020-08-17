package com.blndr.nabeletebackend.repository;

import com.blndr.nabeletebackend.model.Holders.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Integer> {
    Optional<RegistrationRequest> findRegistrationRequestByUserEmail(String email);

    void deleteAllByExpirationTimeBefore(Date expirationTime);

    Optional<RegistrationRequest> findRegistrationRequestByUuid(String uuid);
}
