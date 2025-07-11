package com.example.message_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeenByResponse {
    private String userId;
    private String name;
    private String avatar;
    private LocalDateTime seenAt;
}
