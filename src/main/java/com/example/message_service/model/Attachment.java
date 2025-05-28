package com.example.message_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "attachments")
@Data
public class Attachment {

    @Id
    @Column(length = 36)
    private String id; // ID file đính kèm

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    @JsonIgnore
    private Message message; // Tin nhắn mà file này liên kết

    @Column(nullable = false)
    private String fileUrl; // Đường dẫn URL của file

    @Column(nullable = false)
    private String fileType; // Loại file (image, video, etc.)

    @Column(nullable = false)
    private long fileSize; // Kích thước file
}
