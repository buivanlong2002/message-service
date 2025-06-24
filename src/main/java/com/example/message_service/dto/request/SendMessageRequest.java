package com.example.message_service.dto.request;

import com.example.message_service.model.MessageType;
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
    private String receiverId;
    private String content;
    private MessageType messageType;
    private String replyToMessageId;
}
