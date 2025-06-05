package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.EditMessageRequest;
import com.example.message_service.dto.request.SendMessageRequest;
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.model.Message;
import com.example.message_service.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Gửi tin nhắn mới
    @PostMapping("/send")
    public ApiResponse<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        return messageService.sendMessage(request);
    }

    // Lấy tất cả tin nhắn trong một cuộc trò chuyện
    @PostMapping("/get-by-conversation")
    public ApiResponse<List<MessageResponse>> getMessagesByConversation(@RequestParam String conversationId) {
        return messageService.getMessagesByConversation(conversationId);
    }

    // Lấy tin nhắn theo ID trong một cuộc trò chuyện
    @PostMapping("/get-by-id")
    public ApiResponse<Message> getMessageByIdAndConversation(
            @RequestParam String messageId,
            @RequestParam String conversationId) {

        Optional<Message> message = messageService.getMessageByIdAndConversation(messageId, conversationId);

        return message.map(m -> ApiResponse.success("00", "Lấy tin nhắn thành công", m))
                .orElseGet(() -> ApiResponse.error("01", "Không tìm thấy tin nhắn"));
    }


    // Lấy các tin nhắn theo người gửi trong một cuộc trò chuyện
    @PostMapping("/get-by-sender")
    public ApiResponse<List<MessageResponse>> getMessagesBySenderAndConversation(@RequestParam String senderId, @RequestParam String conversationId) {
        return messageService.getMessagesBySenderAndConversation(conversationId, senderId);
    }

    // Chỉnh sửa nội dung tin nhắn
    @PostMapping("/edit")
    public ApiResponse<MessageResponse> editMessage(@RequestBody EditMessageRequest request) {
        return messageService.editMessage(request.getMessageId(), request.getNewContent());
    }
}
