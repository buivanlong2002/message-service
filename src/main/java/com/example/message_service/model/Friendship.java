package com.example.message_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "friendships")
@Data
public class Friendship {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User sender; // Người gửi lời mời kết bạn

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    @JsonIgnore
    private User receiver; // Người nhận lời mời kết bạn

    @Column(nullable = false)
    private String status = "pending"; // Trạng thái kết bạn (pending, accepted, blocked)

    @Column(nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now(); // Thời điểm gửi lời mời

    private LocalDateTime acceptedAt; // Thời điểm chấp nhận lời mời

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Mã định danh duy nhất cho mối quan hệ kết bạn
}
