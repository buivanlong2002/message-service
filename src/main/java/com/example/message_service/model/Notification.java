package com.example.message_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user; // Người nhận thông báo

    @Column(nullable = false)
    private String type; // Loại thông báo (ví dụ: "message", "friend_request", ...)

    @Column(nullable = false)
    private String content; // Nội dung thông báo

    private String extraData; // Dữ liệu bổ sung (ví dụ: ID cuộc trò chuyện)

    @Column(nullable = false)
    private boolean read;

    private LocalDateTime readAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }
}
