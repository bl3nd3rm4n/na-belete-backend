package com.blndr.nabeletebackend.controller;

import com.blndr.nabeletebackend.model.Holders.AuthenticationRequest;
import com.blndr.nabeletebackend.model.Holders.AuthenticationResponse;
import com.blndr.nabeletebackend.model.Holders.RegistrationRequest;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class Controller {
    @Autowired
    UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/")
    public String home() {
        return ("<h1>Welcome</h1>");
    }

    @GetMapping("/user")
    public String user(Authentication authentication) {

        return ("<h1>Welcome User " + authentication.getName() + " " + authentication.getCredentials() + "</h1>");
    }

    @GetMapping("/admin")
    public String admin() {
        return ("<h1>Welcome Admin</h1>");
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect username or password");
        }
        final UserDetails userDetails = authService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.status(HttpStatus.OK).body(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody User user) {
        if (userService.findUserByEmail(user.getEmail()).isPresent()) {//unregistered
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already registered.");
        }
        RegistrationRequest registrationRequest = userService.register(user);
        notificationService.sendRegistrationConfirmationEmail(registrationRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Registration request sent.");
    }

    @GetMapping("/confirm-registration/{uuid}")
    public ResponseEntity confirmRegistration(@Valid @PathVariable String uuid) {
        return userService.confirmRegistration(uuid);
    }

    @PostMapping("/update-user-profile")
    public ResponseEntity updateUserProfile(@Valid @RequestBody User user, Authentication authentication) {
        if (user.getEmail().equals(authentication.getName())) {
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Updated");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
    }
}
