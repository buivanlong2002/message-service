package com.example.message_service.controller;

import com.example.message_service.model.Conversation;
import com.example.message_service.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    // Lấy tất cả cuộc trò chuyện của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Conversation>> getConversations(@PathVariable String userId) {
        List<Conversation> conversations = conversationService.getConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    // Lấy cuộc trò chuyện theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable String id) {
        Optional<Conversation> conversation = conversationService.getConversationById(id);
        return conversation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Cập nhật cuộc trò chuyện (chỉnh sửa tên hoặc nhóm/cá nhân)
    @PutMapping("/{id}/update")
    public ResponseEntity<Conversation> updateConversation(@PathVariable String id,
                                                           @RequestParam String name,
                                                           @RequestParam boolean isGroup) {
        Conversation updatedConversation = conversationService.updateConversation(id, name, isGroup);
        return ResponseEntity.ok(updatedConversation);
    }

    // Lưu trữ cuộc trò chuyện
    @PutMapping("/{id}/archive")
    public ResponseEntity<String> archiveConversation(@PathVariable String id) {
        conversationService.archiveConversation(id);
        return ResponseEntity.ok("Conversation archived");
    }
}

