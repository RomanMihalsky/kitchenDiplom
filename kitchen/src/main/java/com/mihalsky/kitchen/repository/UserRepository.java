package com.mihalsky.kitchen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mihalsky.kitchen.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}