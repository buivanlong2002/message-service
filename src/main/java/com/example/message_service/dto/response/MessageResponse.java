package com.example.message_service.dto.response;

import com.example.message_service.model.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String id;
    private String conversationId;
    private String senderId;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private String replyToId;
    private boolean edited;

    private List<Attachment> attachments;
}
