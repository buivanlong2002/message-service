package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.MessageFetchRequest;
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.service.MessageService;
import com.example.message_service.service.util.PushNewMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final MessageService messageService;
    private final PushNewMessage pushNewMessage;

    /**
     * Lấy danh sách cuộc trò chuyện của user thông qua WebSocket.
     * Client gửi: /app/conversations/get
     * Server trả về: /topic/conversations/{userId}
     */
    @MessageMapping("/conversations/get")
    public void fetchUserConversations(@Payload String userId) {
        if (userId != null && userId.startsWith("\"") && userId.endsWith("\"")) {
            userId = userId.substring(1, userId.length() - 1);
        }

        if (userId == null || userId.isEmpty()) {
            log.warn("userId không hợp lệ: {}", userId);
            return;
        }

        log.info("WebSocket yêu cầu danh sách cuộc trò chuyện cho userId={}", userId);
        pushNewMessage.pushUpdatedConversationsToUser(userId);
    }

    /**
     * Lấy danh sách tin nhắn của một cuộc trò chuyện có phân trang.
     * Client gửi: /app/messages/get
     * Server trả về: /topic/messages/{conversationId}/{userId}
     */
    @MessageMapping("/messages/get")
    public void fetchMessagesInConversation(@Payload MessageFetchRequest request) {
        String conversationId = request.getConversationId();
        String userId = request.getUserId();
        int page = request.getPage();
        int size = request.getSize();
        String afterTimestamp = request.getAfterTimestamp(); // Thêm tham số này

        if (conversationId == null || userId == null || conversationId.isEmpty() || userId.isEmpty()) {
            log.warn("conversationId hoặc userId không hợp lệ: conversationId={}, userId={}", conversationId, userId);
            return;
        }

        log.info("WebSocket yêu cầu tin nhắn cho conversationId={} từ userId={} (page={}, size={}, afterTimestamp={})",
                conversationId, userId, page, size, afterTimestamp);

        ApiResponse<List<MessageResponse>> response = messageService.getMessagesByConversation(
                conversationId, page, size);

        String destination = "/topic/messages/" + conversationId + "/" + userId;
        if (response.getData() != null && !response.getData().isEmpty()) {
            pushNewMessage.pushMessagesOfConversationToUser(userId, conversationId);
        } else {
            log.warn("Không có tin nhắn nào cho conversationId={} và userId={}", conversationId, userId);
            pushNewMessage.pushMessagesOfConversationToUser(userId, conversationId);
        }
    }
}