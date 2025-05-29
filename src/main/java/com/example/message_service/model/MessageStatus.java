package com.example.message_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "message_status")
@Data
public class MessageStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    @JsonIgnore
    private Message message; // Tin nhắn liên kết với trạng thái

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user; // Người nhận thông báo trạng thái

    @Column(nullable = false)
    private String status = "delivered"; // Trạng thái tin nhắn (delivered, seen, etc.)

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // Thời gian cập nhật trạng thái
}
