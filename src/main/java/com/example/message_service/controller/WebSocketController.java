package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.response.ConversationResponse;
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Nhận yêu cầu từ client để lấy danh sách cuộc trò chuyện (qua WebSocket)
     */
    @MessageMapping("/conversations/get")
    public void fetchUserConversations(@Payload String userId) {
        // Xử lý nếu payload bị truyền dưới dạng chuỗi có dấu ngoặc kép
        if (userId != null && userId.startsWith("\"") && userId.endsWith("\"")) {
            userId = userId.substring(1, userId.length() - 1);
        }

        pushUpdatedConversationsToUser(userId);
    }

    /**
     * Gửi danh sách cuộc trò chuyện của người dùng về client
     */
    public void pushUpdatedConversationsToUser(String userId) {
        ApiResponse<List<ConversationResponse>> response = conversationService.getConversationsByUser(userId);
        if (response.getData() != null) {
            String destination = "/topic/conversations/" + userId;
            messagingTemplate.convertAndSend(destination, response.getData());
            log.info("Đã gửi danh sách cuộc trò chuyện tới {}", destination);
        } else {
            log.warn("Không có dữ liệu cuộc trò chuyện cho userId: {}", userId);
        }
    }

    /**
     * Gửi tin nhắn mới đến tất cả người tham gia trong cuộc trò chuyện
     */
    public void pushNewMessageToConversation(String conversationId, MessageResponse message) {
        String destination = "/topic/conversations/" + conversationId;
        messagingTemplate.convertAndSend(destination, message);
        log.info("Đã gửi tin nhắn mới tới {}", destination);
    }
}
