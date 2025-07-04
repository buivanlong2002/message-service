package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.dto.response.ConversationResponse;
import com.example.message_service.model.Conversation;
import com.example.message_service.service.ConversationMemberService;
import com.example.message_service.service.ConversationService;
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
    private ConversationMemberService conversationMemberService;

    // Tạo nhóm mới
    @PostMapping("/create-group")
    public ResponseEntity<Conversation> createGroupConversation(
            @RequestParam String name,
            @RequestParam String createdBy) {
        Conversation conversation = conversationService.createGroupConversation(name, createdBy);
        return new ResponseEntity<>(conversation, HttpStatus.CREATED);
    }

    // Tạo hoặc lấy cuộc trò chuyện 1-1
    @PostMapping("/one-to-one")
    public ResponseEntity<Conversation> getOrCreateOneToOneConversation(
            @RequestParam String senderId,
            @RequestParam String receiverId) {
        Conversation conversation = conversationService.getOrCreateOneToOneConversation(senderId, receiverId);
        return new ResponseEntity<>(conversation, HttpStatus.OK);
    }

    // Cập nhật tên hoặc trạng thái nhóm
    @PutMapping("/{conversationId}/update")
    public ResponseEntity<ApiResponse<ConversationResponse>> updateConversation(
            @PathVariable String conversationId,
            @RequestBody UpdateConversationRequest request) {
        ApiResponse<ConversationResponse> response = conversationService.updateConversation(conversationId, request);
        return ResponseEntity.ok(response);
    }

    // Lưu trữ cuộc trò chuyện (ẩn)
    @PutMapping("/{conversationId}/archive")
    public ResponseEntity<String> archiveConversation(@PathVariable String conversationId) {
        conversationService.archiveConversation(conversationId);
        return ResponseEntity.ok("Conversation archived");
    }

    // Upload avatar nhóm (chỉ người tạo nhóm được phép)
    @PostMapping("/groups/{id}/avatar")
    public ResponseEntity<ApiResponse<String>> uploadGroupAvatar(@PathVariable String id,
                                                                 @RequestParam("file") MultipartFile file) {
        ApiResponse<String> response = conversationService.updateGroupAvatar(id, file);
        HttpStatus status = response.getStatus().isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    // Lấy tất cả cuộc trò chuyện của người dùng (không phân trang)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversationsByUser(
            @PathVariable String userId) {
        ApiResponse<List<ConversationResponse>> response = conversationService.getConversationsByUser(userId);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách cuộc trò chuyện có phân trang
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getPagedConversationsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ApiResponse<List<ConversationResponse>> response =
                conversationService.getConversationsByUserPaged(userId, page, size);

        return ResponseEntity.ok(response);
    }
}
