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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

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
