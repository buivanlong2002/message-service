package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.response.ConversationResponse;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.model.Conversation;
import com.example.message_service.service.ConversationService;
import com.example.message_service.service.ConversationMemberService; // Thêm service để lấy nhóm theo người dùng
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ConversationMemberService conversationMemberService; // Thêm ConversationMemberService để gọi các phương thức liên quan đến thành viên trong nhóm

    // Tạo cuộc trò chuyện
    @PostMapping("/create-group")
    public ResponseEntity<Conversation> createGroupConversation(
            @RequestParam String name,
            @RequestParam String createdBy) {

        Conversation conversation = conversationService.createGroupConversation(name, createdBy);
        return new ResponseEntity<>(conversation, HttpStatus.CREATED);
    }

    @PostMapping("/one-to-one")
    public ResponseEntity<Conversation> getOrCreateOneToOneConversation(
            @RequestParam String senderId,
            @RequestParam String receiverId) {

        Conversation conversation = conversationService.getOrCreateOneToOneConversation(senderId, receiverId);
        return new ResponseEntity<>(conversation, HttpStatus.OK);
    }


    // Cập nhật cuộc trò chuyện (chỉnh sửa tên hoặc nhóm/cá nhân)
    @PutMapping("/{conversationId}/update")
    public ResponseEntity<ApiResponse<ConversationResponse>> updateConversation(
            @PathVariable String conversationId,
            @RequestBody UpdateConversationRequest request) {

        ApiResponse<ConversationResponse> response = conversationService.updateConversation(conversationId, request);
        return ResponseEntity.ok(response);
    }

    // Lưu trữ cuộc trò chuyện
    @PutMapping("/{conversationId}/archive")
    public ResponseEntity<String> archiveConversation(@PathVariable String conversationId) {
        conversationService.archiveConversation(conversationId);
        return ResponseEntity.ok("Conversation archived");
    }

    // Lấy danh sách các nhóm từ người dùng (bao gồm nhóm người tạo và nhóm người tham gia)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversationsByUser(
            @PathVariable String userId) {

        // Gọi service để lấy tất cả conversations của người dùng
        ApiResponse<List<ConversationResponse>> response = conversationService.getConversationsByUser(userId);

        // Trả về toàn bộ bao gồm cả nhóm và 1-1
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<?> uploadGroupAvatar(@PathVariable String id,
                                               @RequestParam("file") MultipartFile file) {
        ApiResponse<String> response = conversationService.updateGroupAvatar(id, file);
        return ResponseEntity.status(response.getStatus().isSuccess() ? 200 : 400).body(response);
    }


}
