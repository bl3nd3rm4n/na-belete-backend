package com.blndr.nabeletebackend.services;

import com.blndr.nabeletebackend.model.Holders.RegistrationRequest;
import com.blndr.nabeletebackend.model.User;
import com.blndr.nabeletebackend.repository.RegistrationRequestRepository;
import com.blndr.nabeletebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.rmi.NoSuchObjectException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RegistrationRequestRepository registrationRequestRepository;

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> deleteUserByEmail(String email) throws NoSuchObjectException {
        return userRepository.deleteUserByEmail(email);
    }

    public User saveUser(User user) {
        user = userRepository.save(user);
        return user;
    }

    public RegistrationRequest register(User user) {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (registrationRequestRepository.findRegistrationRequestByUuid(uuid).isPresent());
        return registrationRequestRepository.save(new RegistrationRequest(saveUser(user), uuid));
    }

    public ResponseEntity confirmRegistration(String uuid) {
        Optional<RegistrationRequest> registrationRequest = registrationRequestRepository.findRegistrationRequestByUuid(uuid);
        if (registrationRequest.isPresent()) {
            User user = registrationRequest.get().getUser();
            user.setEnabled(true);
            saveUser(user);
            registrationRequestRepository.delete(registrationRequest.get());
            return ResponseEntity.status(HttpStatus.OK).body("User registration confirmed.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not registered/registration link expired.");
        }
    }
}

