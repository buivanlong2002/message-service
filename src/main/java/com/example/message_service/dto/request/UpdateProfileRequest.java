package com.example.message_service.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String displayName;
    private String phone;
    private String email;
    private String avatarUrl;
}
