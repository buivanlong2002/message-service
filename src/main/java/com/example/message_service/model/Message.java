package com.example.message_service.model;

import jakarta.persistence.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Message {

    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;  // Mối quan hệ với cuộc trò chuyện

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;  // Người gửi

    private String content;  // Nội dung tin nhắn

    @Enumerated(EnumType.STRING)
    private TrayIcon.MessageType messageType;  // Loại tin nhắn (text, image, file...)

    private LocalDateTime createdAt;  // Thời gian gửi

    @ManyToOne
    @JoinColumn(name = "reply_to")
    private Message replyTo;  // Nếu tin nhắn này là trả lời tin nhắn khác

    private boolean isEdited;  // Kiểm tra xem tin nhắn đã được chỉnh sửa chưa

    public String getId() {
        return id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public User getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public TrayIcon.MessageType getMessageType() {
        return messageType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Message getReplyTo() {
        return replyTo;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMessageType(TrayIcon.MessageType messageType) {
        this.messageType = messageType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReplyTo(Message replyTo) {
        this.replyTo = replyTo;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }
}
