package com.example.message_service.mapper;

import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getContent(),
                message.getMessageType().name(),
                message.getCreatedAt(),
                message.getReplyTo() != null ? message.getReplyTo().getId() : null,
                message.isEdited()
        );
    }
}

