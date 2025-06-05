package com.example.message_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Message {


    @Id
    private String id;

    @PrePersist
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;  // Người gửi

    private String content;  // Nội dung tin nhắn

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;  // Loại tin nhắn (text, image, file...)

    private LocalDateTime createdAt;  // Thời gian gửi

    @ManyToOne
    @JoinColumn(name = "reply_to")
    private Message replyTo;  // Nếu tin nhắn này là trả lời tin nhắn khác

    private boolean isEdited;  // Kiểm tra xem tin nhắn đã được chỉnh sửa chưa

}
