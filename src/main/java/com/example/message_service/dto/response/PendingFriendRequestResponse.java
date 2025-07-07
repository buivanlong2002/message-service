package com.example.message_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PendingFriendRequestResponse {
    private String senderId;
    private String senderDisplayName;
    private String senderAvatarUrl;

    private String receiverId;              // ✅ Thêm trường này
    private String receiverDisplayName;     // (tuỳ chọn)
    private String receiverAvatarUrl;       // (tuỳ chọn)

    private LocalDateTime requestedAt;
}
