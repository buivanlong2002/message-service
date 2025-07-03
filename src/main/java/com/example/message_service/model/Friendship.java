package com.example.message_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// GỢI Ý 4: Thay thế @Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friendships", indexes = {
        // GỢI Ý 2: Thêm index để tra cứu nhanh theo từng người dùng
        @Index(name = "idx_friendship_user_id", columnList = "user_id"),
        @Index(name = "idx_friendship_friend_id", columnList = "friend_id")
})
// GỢI Ý 1: Sử dụng IdClass để định nghĩa khóa chính tổng hợp
@IdClass(FriendshipId.class)
public class Friendship {

    // GỢI Ý 1 & 4: Định nghĩa các phần của khóa chính
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User sender; // Người gửi lời mời kết bạn

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User receiver; // Người nhận lời mời kết bạn


    @Column(nullable = false)
    private String status = "pending"; // Trạng thái kết bạn (pending, accepted, blocked)

    // GỢI Ý 3: Tự động hóa thời gian tạo
    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt; // Thời điểm gửi lời mời

    private LocalDateTime acceptedAt; // Thời điểm chấp nhận lời mời

    // Không cần trường id riêng nữa vì đã có composite key
    // @Id
    // @GeneratedValue(strategy = GenerationType.UUID)
    // private String id;
}