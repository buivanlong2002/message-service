package com.example.message_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditMessageRequest {
    private String messageId;
    private String newContent;

    // Getters & Setters
}
