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
    private String id;

    @PrePersist
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    @JsonIgnore
    private Message message; // Tin nhắn mà file này liên kết

    @Column(nullable = false)
    private String fileUrl; // Đường dẫn URL của file (đã encode + UUID)

    @Column(nullable = false)
    private String fileType; // Loại file (image, video, etc.)

    @Column(nullable = false)
    private long fileSize; // Kích thước file

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName; // Tên file thật người dùng upload
}
