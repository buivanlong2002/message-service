package com.example.message_service.controller;


import com.example.message_service.service.ConversationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {

    private final ConversationService conversationService;

    public WebSocketController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @MessageMapping("/conversations/get")
    public void fetchUserConversations(String userId) {
        System.out.println("⚠️ Nhận userId trực tiếp từ client: " + userId);
        conversationService.pushUpdatedConversationsToUser(userId);
    }

}
