package com.example.message_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
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
    private String replyToContent;
    private String replyToSenderName;

    private boolean edited;
    private boolean seen;
    private boolean recalled;
    private List<AttachmentResponse> attachments;
    private String timeAgo;
    private String status; // "SEEN", "SENT", "FAILED"
    private List<SeenByResponse> seenBy;
}
