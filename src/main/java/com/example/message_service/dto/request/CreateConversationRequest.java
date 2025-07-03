package com.example.message_service.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CreateConversationRequest {
    private boolean isGroup; // true: nhóm, false: 1-1
    private String name; // Tên nhóm (nếu isGroup = true)
    private String createdBy; // ID người tạo
    private List<String> memberIds; // Danh sách ID các thành viên để thêm vào (bao gồm cả người tạo)
}