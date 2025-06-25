package com.example.message_service.mapper;

import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.dto.response.SenderResponse;
import com.example.message_service.model.Message;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class MessageMapper {

    public MessageResponse toMessageResponse(Message message) {
        SenderResponse senderResponse = new SenderResponse(
                message.getSender().getId(),
                message.getSender().getDisplayName(),
                message.getSender().getAvatarUrl()
        );

        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                senderResponse,
                message.getContent(),
                message.getMessageType().name(),
                message.getCreatedAt(),
                message.getReplyTo() != null ? message.getReplyTo().getId() : null,
                message.isEdited(),
                message.getAttachments(),
                getTimeAgo(message.getCreatedAt()) // nếu bạn tính sẵn
        );
    }



    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa xong";
        if (duration.toHours() < 1) return duration.toMinutes() + " phút trước";
        if (duration.toDays() < 1) return duration.toHours() + " giờ trước";
        return duration.toDays() + " ngày trước";
    }
}
