package com.example.message_service.mapper;

import com.example.message_service.dto.response.*;
import com.example.message_service.model.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {

    public MessageResponse toMessageResponse(Message message) {
        SenderResponse senderResponse = new SenderResponse(
                message.getSender().getId(),
                message.getSender().getDisplayName(),
                message.getSender().getAvatarUrl()
        );

        String replyToId = null;
        String replyToContent = null;
        String replyToSenderName = null;

        if (message.getReplyTo() != null) {
            replyToId = message.getReplyTo().getId();
            replyToContent = message.getReplyTo().getContent();
            replyToSenderName = message.getReplyTo().getSender().getDisplayName();
        }

        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                senderResponse,
                message.getContent(),
                message.getMessageType().name(),
                message.getCreatedAt(),
                replyToId,
                replyToContent,
                replyToSenderName,
                message.isEdited(),
                message.isSeen(),
                message.isRecalled(),
                toAttachmentResponseList(message.getAttachments()),
                getTimeAgo(message.getCreatedAt()),
                null, // status sẽ được set nếu biết userId
                null  // seenBy không có do không lưu người đã xem
        );
    }

    public MessageResponse toMessageResponse(Message message, String currentUserId) {
        MessageResponse response = toMessageResponse(message);

        // Gán status theo người hiện tại
        if (message.getSender().getId().equals(currentUserId)) {
            response.setStatus("SENT");
        } else if (message.isSeen()) {
            response.setStatus("SEEN");
            response.setSeen(true);
        } else {
            response.setStatus("DELIVERED");
        }

        // Không có seenBy cụ thể nếu không lưu trong bảng phụ
        response.setSeenBy(null);

        return response;
    }

    private List<AttachmentResponse> toAttachmentResponseList(List<Attachment> attachments) {
        if (attachments == null || attachments.isEmpty()) return List.of();

        return attachments.stream()
                .map(att -> new AttachmentResponse(
                        att.getId(),
                        att.getOriginalFileName(),
                        att.getFileUrl(),
                        att.getFileType()
                ))
                .collect(Collectors.toList());
    }

    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        if (duration.toMinutes() < 1) return "Vừa xong";
        if (duration.toHours() < 1) return duration.toMinutes() + " phút trước";
        if (duration.toDays() < 1) return duration.toHours() + " giờ trước";
        return duration.toDays() + " ngày trước";
    }
}
