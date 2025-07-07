package com.example.message_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockedUserResponse {
    private String id;
    private String displayName;
    private String avatarUrl;
}
