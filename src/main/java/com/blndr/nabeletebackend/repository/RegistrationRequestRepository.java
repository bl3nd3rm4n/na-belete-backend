package com.blndr.nabeletebackend.repository;

import com.blndr.nabeletebackend.model.RegistrationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationCode, Integer> {
    Optional<RegistrationCode> findRegistrationRequestByUserUsername(String username);

    void deleteAllByExpirationTimeBefore(Date expirationTime);

    Optional<RegistrationCode> findRegistrationRequestByUuid(String uuid);
}
