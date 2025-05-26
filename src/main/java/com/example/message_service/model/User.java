package com.example.message_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore  // Không serialize mật khẩu hash ra JSON
    private String passwordHash;

    @Transient  // Trường tạm thời, không lưu vào DB
    @JsonProperty("password")  // Thuộc tính nhận từ JSON là "password"
    private String password;

    private String displayName;

    private String avatarUrl;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private String status = "active";

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}