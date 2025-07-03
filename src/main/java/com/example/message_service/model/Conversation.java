package com.example.message_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// GỢI Ý 2: Thay thế @Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
// GỢI Ý 4: Thêm index cho các cột có thể được tìm kiếm
@Table(name = "conversations", indexes = {
        @Index(name = "idx_conversation_name", columnList = "name"),
        // Index này rất quan trọng để sắp xếp danh sách cuộc trò chuyện
        @Index(name = "idx_conversation_updated_at", columnList = "updatedAt DESC")
})
public class Conversation {

    @Id
    @Column(updatable = false)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Column(name = "name")
    private String name;

    // GỢI Ý: Nên dùng quan hệ @ManyToOne thay vì lưu trực tiếp ID dạng String
    // để tận dụng được sức mạnh của JPA.
    // Tuy nhiên, để không phá vỡ code hiện tại, ta giữ nguyên kiểu String.
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "is_group", nullable = false)
    private boolean isGroup; // Tên biến "group" trùng với từ khóa SQL, đổi thành isGroup an toàn hơn

    // GỢI Ý 3: Tự động hóa thời gian tạo
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // GỢI Ý 5: Thêm trường updatedAt để sắp xếp các cuộc trò chuyện.
    // Trường này sẽ được cập nhật mỗi khi có tin nhắn mới hoặc thay đổi trong nhóm.
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isArchived = false;

    private String avatarUrl;

    // --- GỢI Ý 5 & 1: Thêm các quan hệ cần thiết để tối ưu ---

    // Quan hệ tới tin nhắn cuối cùng. Dùng LAZY để chỉ load khi cần.
    // Dùng @JoinColumn để chỉ định đây là phía sở hữu quan hệ.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    // Quan hệ tới danh sách thành viên.
    // mappedBy chỉ ra rằng ConversationMember là bên quản lý quan hệ này.
    @OneToMany(
            mappedBy = "conversation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ConversationMember> members = new ArrayList<>();

}