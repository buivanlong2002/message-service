package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.EditMessageRequest;
import com.example.message_service.dto.request.SendMessageRequest; // Import DTO mới
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api") // Chuyển request mapping gốc lên đây
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Lấy lịch sử tin nhắn của một cuộc trò chuyện (có phân trang).
     * GET /api/conversations/{conversationId}/messages
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ApiResponse<List<MessageResponse>> response = messageService.getMessagesByConversation(conversationId, page, size);
        return buildResponse(response);
    }

    /**
     * Gửi một tin nhắn mới.
     * Sử dụng DTO để gói các tham số, giúp API sạch sẽ hơn.
     * POST /api/messages
     */
    @PostMapping(value = "/messages", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @RequestPart("request") SendMessageRequest request, // Nhận DTO dưới dạng JSON
            @RequestPart(value = "files", required = false) MultipartFile[] files // Nhận file
    ) {
        ApiResponse<MessageResponse> response = messageService.sendMessage(
                request.getSenderId(),
                request.getConversationId(),
                request.getReceiverId(),
                files,
                request.getMessageType(),
                request.getContent(),
                request.getReplyToId()
        );
        return buildResponse(response);
    }

    /**
     * Chỉnh sửa nội dung một tin nhắn.
     * PUT /api/messages/{messageId}
     */
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponse>> editMessage(
            @PathVariable String messageId,
            @RequestBody EditMessageRequest request) { // EditMessageRequest chỉ cần chứa newContent
        ApiResponse<MessageResponse> response = messageService.editMessage(messageId, request.getNewContent());
        return buildResponse(response);
    }

    /*
     * Các endpoint không cần thiết hoặc ít dùng đã được loại bỏ để API tập trung hơn:
     * - getMessageByIdAndConversation: Nhu cầu lấy 1 tin nhắn đơn lẻ rất hiếm.
     * - getMessagesBySenderAndConversation: Logic này thường được xử lý ở client.
     * Việc giữ API tinh gọn giúp dễ bảo trì và bảo mật hơn.
     */

    /**
     * Phương thức helper để xây dựng ResponseEntity.
     */
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(ApiResponse<T> response) {
        if (response.getStatus().isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}