package com.example.message_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PendingFriendRequestResponse {
    private String senderId;
    private String displayName;
    private String avatarUrl;
    private LocalDateTime requestedAt;
}
