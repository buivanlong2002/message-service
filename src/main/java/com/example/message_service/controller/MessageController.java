package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.model.Message;
import com.example.message_service.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Gửi tin nhắn mới
    @PostMapping
    public ApiResponse<MessageResponse> sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    // Lấy tất cả tin nhắn trong một cuộc trò chuyện (theo thứ tự thời gian)
    @GetMapping("/conversation/{conversationId}")
    public ApiResponse<List<MessageResponse>> getMessagesByConversation(@PathVariable String conversationId) {
        return (ApiResponse<List<MessageResponse>>) messageService.getMessagesByConversation(conversationId);
    }


    // Lấy tin nhắn theo ID và cuộc trò chuyện
    @GetMapping("/conversation/{conversationId}/message/{id}")
    public Optional<Message> getMessageByIdAndConversation(@PathVariable String id, @PathVariable String conversationId) {
        return messageService.getMessageByIdAndConversation(id, conversationId);
    }

    // Lấy tin nhắn của một người gửi trong một cuộc trò chuyện
    @GetMapping("/conversation/{conversationId}/sender/{senderId}")
    public List<Message> getMessagesBySenderAndConversation(@PathVariable String senderId, @PathVariable String conversationId) {
        return messageService.getMessagesBySenderAndConversation(senderId, conversationId);
    }

    // Chỉnh sửa tin nhắn
    @PutMapping("/{messageId}")
    public Message editMessage(@PathVariable String messageId, @RequestBody String newContent) {
        return messageService.editMessage(messageId, newContent);
    }
}
