package com.example.message_service.dto.response;

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
    private SenderResponse sender;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private String replyToId;
    private boolean edited;
    private boolean seen;
    private boolean recalled;
    private List<AttachmentResponse> attachments;
    private String timeAgo;
}
