package com.example.message_service.model;
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
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    @Column(unique = true, nullable = false)
    private String username;

    private String passwordHash;

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