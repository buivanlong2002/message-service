package com.example.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDTO {
    private String senderName;
    private LocalDateTime requestedAt;
}

