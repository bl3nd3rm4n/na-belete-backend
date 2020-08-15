package com.blndr.nabeletebackend.repository;

import com.blndr.nabeletebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String Email);

    Optional<User> deleteByEmail(String Email);
}
