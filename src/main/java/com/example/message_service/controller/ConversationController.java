package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.ConversationDTO;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.model.Conversation;
import com.example.message_service.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    // Tạo cuộc trò chuyện
    @PostMapping("/create")
    public ResponseEntity<Conversation> createConversation(@RequestParam String name,
                                                           @RequestParam boolean isGroup,
                                                           @RequestParam String createdBy) {
        Conversation conversation = conversationService.createConversation(name, isGroup, createdBy);
        return new ResponseEntity<>(conversation, HttpStatus.CREATED);
    }


//    // Lấy tất cả cuộc trò chuyện của người dùng
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Conversation>> getConversations(@PathVariable String userId) {
//        List<Conversation> conversations = conversationService.getConversations(userId);
//        return ResponseEntity.ok(conversations);
//    }
//
//    // Lấy cuộc trò chuyện theo ID
//    @GetMapping("/{id}")
//    public ResponseEntity<Conversation> getConversation(@PathVariable String id) {
//        Optional<Conversation> conversation = conversationService.getConversationById(id);
//        return conversation.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//    }

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
}

