package com.example.message_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Conversation {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name; // Tên cuộc trò chuyện (chỉ dành cho nhóm)

    @Column(nullable = false)
    private String createdBy; // Người tạo cuộc trò chuyện

    @Column(nullable = false)
    private boolean isGroup; // Là nhóm (true) hay cá nhân (false)

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời gian tạo cuộc trò chuyện

    @Column(nullable = false)
    private boolean isArchived = false; // Trạng thái lưu trữ cuộc trò chuyện (false mặc định)

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}
