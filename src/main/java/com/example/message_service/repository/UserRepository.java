package com.example.message_service.repository;

import com.example.message_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);  // Tìm theo tên đăng nhập
    Optional<User> findByEmail(String email);  // Tìm theo email
    Optional<User> findByPhoneNumber(String phoneNumber);  // Tìm theo số điện thoại
}
