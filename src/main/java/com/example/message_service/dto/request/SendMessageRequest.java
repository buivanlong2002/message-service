package com.example.message_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    private UUID conversationId;
    private UUID senderId;
    private String content;
    private String replyToMessageId;

    // Getters and Setters
}
