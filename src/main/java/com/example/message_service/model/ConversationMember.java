package com.example.message_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID; // Không cần nữa nếu không dùng id riêng

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// GỢI Ý 2: Thêm các index quan trọng nhất
@Table(name = "conversation_members", indexes = {
        // Index này siêu quan trọng để lấy danh sách chat của một user
        @Index(name = "idx_member_user_id", columnList = "user_id"),
        // Index này quan trọng để lấy danh sách thành viên của một group
        @Index(name = "idx_member_conversation_id", columnList = "conversation_id")
})
// GỢI Ý 1: Sử dụng IdClass để định nghĩa khóa chính tổng hợp
@IdClass(ConversationMemberId.class)
public class ConversationMember {

    // GỢI Ý 1 & 4: Định nghĩa các phần của khóa chính
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // GỢI Ý 3: Tự động hóa thời gian
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(nullable = false)
    private String role = "member"; // Gỡ bỏ columnDefinition để tương thích nhiều DB hơn

    // Không cần trường id riêng nữa
    // @Id
    // private String id;
}