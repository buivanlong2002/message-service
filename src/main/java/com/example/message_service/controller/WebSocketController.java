
package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.MessageFetchRequest;
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.service.ConversationService;
import com.example.message_service.service.MessageService;
import com.example.message_service.service.util.PushNewMessage;
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

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PushNewMessage pushNewMessage;

    /**
     * Nhận yêu cầu từ client để lấy danh sách cuộc trò chuyện (qua WebSocket)
     */
    @MessageMapping("/conversations/get")
    public void fetchUserConversations(@Payload String userId) {
        if (userId != null && userId.startsWith("\"") && userId.endsWith("\"")) {
            userId = userId.substring(1, userId.length() - 1);
        }
        System.out.println("Fetching user conversations for: " + userId);
        pushNewMessage.pushUpdatedConversationsToUser(userId);
    }

    /**
     * Lấy danh sách tin nhắn của một cuộc trò chuyện qua WebSocket
     */
    @MessageMapping("/messages/get")
    public void fetchMessagesInConversation(@Payload MessageFetchRequest request) {
        String conversationId = request.getConversationId();
        int page = request.getPage();
        int size = request.getSize();

        ApiResponse<List<MessageResponse>> response =
                messageService.getMessagesByConversation(conversationId, page, size);

        String destination = "/topic/messages/" + conversationId;
        messagingTemplate.convertAndSend(destination, response.getData());
        System.out.println("Fetching messages for: " + response.getData());
        log.info("Đã gửi danh sách tin nhắn tới {}", destination);
    }
}
