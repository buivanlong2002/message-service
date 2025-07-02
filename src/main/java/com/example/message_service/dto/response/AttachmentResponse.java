package com.example.message_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponse {
    private String id;
    private String fileName;
    private String url;
    private String fileType;
}
