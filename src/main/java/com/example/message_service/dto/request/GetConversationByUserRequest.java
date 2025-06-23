package com.example.message_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class GetConversationByUserRequest {
    private UUID userId;
}
