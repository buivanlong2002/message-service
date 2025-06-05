package com.example.message_service.dto.request;

import java.util.UUID;

public class GetMembersByConversationRequest {
    private UUID conversationId;

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }
}

