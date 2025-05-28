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
    @Column(length = 36)
    private String id; // ID thông báo

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user; // Người nhận thông báo

    @Column(nullable = false)
    private String type; // Loại thông báo (ví dụ: "message", "friend_request", ...)

    @Column(nullable = false)
    private String content; // Nội dung thông báo

    private String extraData; // Dữ liệu bổ sung (ví dụ: thông tin về tin nhắn, cuộc trò chuyện)

    @Column(nullable = false)
    private boolean read; // Trạng thái thông báo (đã đọc hay chưa)

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo thông báo

}
