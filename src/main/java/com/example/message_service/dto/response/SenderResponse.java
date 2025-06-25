package com.example.message_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SenderResponse {
    private String senderId;
    private String nameSender ;
    private String avatarSender;
}
