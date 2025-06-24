package com.example.message_service.mapper;

import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.model.Message;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class MessageMapper {

    public MessageResponse toMessageResponse(Message message) {
        MessageResponse dto = new MessageResponse();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType().name());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setReplyToId(message.getReplyTo() != null ? message.getReplyTo().getId() : null);
        dto.setEdited(message.isEdited());
        dto.setAttachments(message.getAttachments());

        // Tính timeAgo
        dto.setTimeAgo(getTimeAgo(message.getCreatedAt()));

        return dto;
    }

    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa xong";
        if (duration.toHours() < 1) return duration.toMinutes() + " phút trước";
        if (duration.toDays() < 1) return duration.toHours() + " giờ trước";
        return duration.toDays() + " ngày trước";
    }
}
