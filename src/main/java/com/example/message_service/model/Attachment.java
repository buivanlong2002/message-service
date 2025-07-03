package com.example.message_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

// GỢI Ý 2: Thay thế @Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
// GỢI Ý 1: Thêm index trên foreign key
@Table(name = "attachments", indexes = {
        @Index(name = "idx_attachment_message_id", columnList = "message_id")
})
public class Attachment {

    @Id
    @Column(updatable = false)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // GỢI Ý 3: Đảm bảo quan hệ này không thể thay đổi sau khi tạo
    @ManyToOne(fetch = FetchType.LAZY) // Luôn ưu tiên LAZY cho @ManyToOne
    @JoinColumn(name = "message_id", nullable = false, updatable = false)
    @JsonIgnore
    private Message message; // Tin nhắn mà file này liên kết

    // GỢI Ý 4: Chỉ định độ dài lớn hơn cho URL để tránh lỗi
    @Column(name = "file_url", nullable = false, updatable = false, length = 1024)
    private String fileUrl; // Đường dẫn URL của file (đã encode + UUID)

    @Column(name = "file_type", nullable = false, updatable = false)
    private String fileType; // Loại file (image, video, etc.)

    @Column(name = "file_size", nullable = false, updatable = false)
    private long fileSize; // Kích thước file

    @Column(name = "original_file_name", nullable = false, updatable = false, length = 512)
    private String originalFileName; // Tên file thật người dùng upload
}