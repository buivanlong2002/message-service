package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.ConversationDTO;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.model.Conversation;
import com.example.message_service.service.ConversationService;
import com.example.message_service.service.ConversationMemberService; // Thêm service để lấy nhóm theo người dùng
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ConversationMemberService conversationMemberService; // Thêm ConversationMemberService để gọi các phương thức liên quan đến thành viên trong nhóm

    // Tạo cuộc trò chuyện
    @PostMapping("/create")
    public ResponseEntity<Conversation> createConversation(@RequestParam String name,
                                                           @RequestParam boolean isGroup,
                                                           @RequestParam String createdBy) {
        Conversation conversation = conversationService.createConversation(name, isGroup, createdBy);
        return new ResponseEntity<>(conversation, HttpStatus.CREATED);
    }

    // Cập nhật cuộc trò chuyện (chỉnh sửa tên hoặc nhóm/cá nhân)
    @PutMapping("/{conversationId}/update")
    public ResponseEntity<ApiResponse<ConversationDTO>> updateConversation(
            @PathVariable String conversationId,
            @RequestBody UpdateConversationRequest request) {

        ApiResponse<ConversationDTO> response = conversationService.updateConversation(conversationId, request);
        return ResponseEntity.ok(response);
    }

    // Lưu trữ cuộc trò chuyện
    @PutMapping("/{conversationId}/archive")
    public ResponseEntity<String> archiveConversation(@PathVariable String conversationId) {
        conversationService.archiveConversation(conversationId);
        return ResponseEntity.ok("Conversation archived");
    }

    // Lấy danh sách các nhóm từ người dùng (bao gồm nhóm người tạo và nhóm người tham gia)
    @GetMapping("/user/{userId}/conversations")
    public ResponseEntity<ApiResponse<List<ConversationDTO>>> getConversationsByUser(
            @PathVariable String userId) {

        // Lấy danh sách các cuộc trò chuyện (bao gồm nhóm người tham gia và nhóm người tạo)
        ApiResponse<List<ConversationDTO>> response = conversationService.getConversationsByUser(userId);

        // Trả về response chứa danh sách nhóm của người dùng
        return ResponseEntity.ok(response);
    }
}
