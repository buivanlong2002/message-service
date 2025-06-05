package com.example.message_service.dto.request;

import java.util.UUID;

public class GetConversationByUserRequest {
    private UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
