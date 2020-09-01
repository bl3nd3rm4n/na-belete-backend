package com.blndr.nabeletebackend.controller;

import com.blndr.nabeletebackend.model.Role;
import com.blndr.nabeletebackend.model.User;
import com.blndr.nabeletebackend.services.AuthService;
import com.blndr.nabeletebackend.services.NotificationService;
import com.blndr.nabeletebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final NotificationService notificationService;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, AuthService authService, NotificationService notificationService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.notificationService = notificationService;
    }

    @GetMapping("/users")
    public ResponseEntity getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUsers());
    }

    @GetMapping("/user")
    public ResponseEntity getUserByUsername(@RequestBody String username, Authentication authentication) {
        if (isOwnerOrAdmin(username, authentication)) {
            if (!userService.findUserByUsername(username).isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
            }
            return ResponseEntity.status(HttpStatus.OK).body(userService.findUserByUsername(username));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }
    }

    @PutMapping("/user")
    public ResponseEntity updateUserByUsername(@Valid @RequestBody User user, Authentication authentication) {
        if (isOwnerOrAdmin(user.getUsername(), authentication)) {
            if (!userService.findUserByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
            }
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Updated");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
    }

    @DeleteMapping("/user")
    public ResponseEntity deleteUserByUsername(@Valid @RequestBody String username, Authentication authentication) {
        if (isOwnerOrAdmin(username, authentication)) {
            if (!userService.findUserByUsername(username).isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
            }
            userService.deleteUserByUsername(username);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
    }

    private boolean isOwnerOrAdmin(@RequestBody @Valid String username, Authentication authentication) {
        return username.equals(authentication.getName()) || ((User) authentication.getPrincipal()).getRole().equals(Role.ADMIN);
    }
}
