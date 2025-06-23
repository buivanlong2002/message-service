package com.example.message_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastMessageInfo {
    private String lastMessageContent;
    private String lastMessageSenderName;
    private String lastMessageTimeAgo;
}
