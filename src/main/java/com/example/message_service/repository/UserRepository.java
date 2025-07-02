package com.example.message_service.repository;

import com.example.message_service.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);  // Tìm theo email
    Optional<User> findByPhoneNumber(String phoneNumber);  // Tìm theo số điện thoại



}
