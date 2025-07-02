package com.example.message_service.service.util;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.response.ConversationResponse;
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNewMessage {

    private final   SimpMessagingTemplate messagingTemplate;
    private final ConversationService conversationService;

    public void pushNewMessageToConversation(String conversationId, MessageResponse message) {
        String destination = "/topic/conversations/" + conversationId;
        messagingTemplate.convertAndSend(destination, message);

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
}
