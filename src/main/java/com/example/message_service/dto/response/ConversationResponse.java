package com.example.message_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponse {
    private String id;

    private String name;

    @JsonProperty("isGroup")
    @Column(name = "is_group", nullable = false)
    private boolean group;

    private LocalDateTime createdAt;

    private LastMessageInfo lastMessage;

    public ConversationResponse(String id, String name, boolean group, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.createdAt = createdAt;
        this.lastMessage = null;
    }

}

