package com.example.message_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class RemoveMemberRequest {
        private UUID conversationId;
        private UUID userId;

        // Constructors, Getters, Setters
    }

