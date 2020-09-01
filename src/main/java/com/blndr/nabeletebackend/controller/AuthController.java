package com.blndr.nabeletebackend.controller;

import com.blndr.nabeletebackend.model.Holders.AuthenticationRequest;
import com.blndr.nabeletebackend.model.Holders.AuthenticationResponse;
import com.blndr.nabeletebackend.model.Holders.RegistrationRequest;
import com.blndr.nabeletebackend.model.RegistrationCode;
import com.blndr.nabeletebackend.model.User;
import com.blndr.nabeletebackend.services.AuthService;
import com.blndr.nabeletebackend.services.NotificationService;
import com.blndr.nabeletebackend.services.UserService;
import com.blndr.nabeletebackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, AuthService authService, NotificationService notificationService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.notificationService = notificationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest) {
        System.out.println(authenticationRequest.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
        final UserDetails userDetails = authService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.status(HttpStatus.OK).body(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        if (userService.findUserByUsername(registrationRequest.getUsername()).isPresent()) {//unregistered
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User is already registered.");
        }
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setAddress(registrationRequest.getAddress());
        RegistrationCode registrationCode = userService.register(user);
        notificationService.sendRegistrationConfirmationEmail(registrationCode);
        return ResponseEntity.status(HttpStatus.OK).body("Registration request sent.");
    }

    @GetMapping("/confirm-registration/{uuid}")
    public ResponseEntity confirmRegistration(@Valid @PathVariable String uuid) {
        return userService.confirmRegistration(uuid);
    }
}
