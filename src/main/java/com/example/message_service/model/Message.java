package com.example.message_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// GỢI Ý 4: Thay thế @Data bằng các annotation cụ thể để tránh lỗi vòng lặp.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
// GỢI Ý 1: Thêm index quan trọng nhất để tăng tốc độ tải lịch sử tin nhắn.
// Index này giúp tìm kiếm theo conversation_id và sắp xếp theo createdAt cực nhanh.
@Table(name = "messages", indexes = {
        @Index(name = "idx_message_conversation_created", columnList = "conversation_id, createdAt DESC")
})
public class Message {

    @Id
    @Column(updatable = false)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // GỢI Ý 3: Thêm FetchType.LAZY để tránh vấn đề N+1 query.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    // GỢI Ý 3: Thêm FetchType.LAZY.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;  // Người gửi

    @Lob // Dùng @Lob cho các trường text có thể dài để DB chọn kiểu dữ liệu phù hợp (TEXT, CLOB).
    private String content;  // Nội dung tin nhắn

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;  // Loại tin nhắn (text, image, file...)

    // GỢI Ý 2: Dùng @CreationTimestamp để DB tự động điền thời gian tạo.
    // Giúp code ở service gọn hơn và đảm bảo dữ liệu luôn đúng.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // Thời gian gửi

    // GỢI Ý 3: Thêm FetchType.LAZY.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to")
    private Message replyTo;  // Nếu tin nhắn này là trả lời tin nhắn khác

    @Column(nullable = false)
    private boolean isEdited = false;  // Khởi tạo giá trị mặc định để tránh NullPointerException

    // Giữ nguyên quan hệ này, FetchType.LAZY đã là mặc định và đúng cho @OneToMany
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    // Helper method để quản lý quan hệ hai chiều (nếu cần)
    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
        attachment.setMessage(this);
    }

    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
        attachment.setMessage(null);
    }
}