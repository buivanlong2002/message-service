package com.example.message_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDTO {
    private String id;

    private String name;

    @JsonProperty("isGroup")
    @Column(name = "is_group", nullable = false)
    private boolean group;

    private LocalDateTime createdAt;
}

