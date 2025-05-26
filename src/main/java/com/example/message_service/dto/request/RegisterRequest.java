package com.example.message_service.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;  // Nếu muốn đổi tên thành password thì sửa cho rõ ràng hơn
    private String displayName;
    private String avatarUrl;
    private String phoneNumber;
    private String email;
}
