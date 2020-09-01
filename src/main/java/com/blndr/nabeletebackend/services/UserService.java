package com.blndr.nabeletebackend.services;

import com.blndr.nabeletebackend.model.RegistrationCode;
import com.blndr.nabeletebackend.model.User;
import com.blndr.nabeletebackend.repository.RegistrationRequestRepository;
import com.blndr.nabeletebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RegistrationRequestRepository registrationRequestRepository;

    @Autowired
    public UserService(UserRepository userRepository, RegistrationRequestRepository registrationRequestRepository) {
        this.userRepository = userRepository;
        this.registrationRequestRepository = registrationRequestRepository;
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public Optional<User> deleteUserByUsername(String username) {
        return userRepository.deleteUserByUsername(username);
    }

    public User saveUser(User user) {
        user = userRepository.save(user);
        return user;
    }

    public RegistrationCode register(User user) {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (registrationRequestRepository.findRegistrationRequestByUuid(uuid).isPresent());
        return registrationRequestRepository.save(new RegistrationCode(saveUser(user), uuid));
    }

    public ResponseEntity confirmRegistration(String uuid) {
        Optional<RegistrationCode> registrationRequest = registrationRequestRepository.findRegistrationRequestByUuid(uuid);
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

    public List<User> findUsers() {
        return userRepository.findAll();
    }
}

