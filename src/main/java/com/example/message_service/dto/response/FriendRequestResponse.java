package com.example.message_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestResponse {
    // ID của người gửi, rất quan trọng để client có thể thực hiện hành động (chấp nhận/từ chối)
    private String senderId;

    // Tên hiển thị của người gửi
    private String senderDisplayName;

    // Avatar của người gửi
    private String senderAvatarUrl;

    // Thời gian gửi lời mời
    private LocalDateTime requestedAt;
}