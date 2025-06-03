package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.mapper.MessageMapper;
import com.example.message_service.model.Message;
import com.example.message_service.repository.ConversationRepository;
import com.example.message_service.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private MessageMapper messageMapper;

    public ApiResponse<MessageResponse> sendMessage(Message message) {
        // Gán ID nếu chưa có
        if (message.getId() == null) {
            message.setId(UUID.randomUUID().toString());
        }

        if (message.getReplyTo() != null && message.getReplyTo().getId() != null) {
            Message replyTo = messageRepository.findById(message.getReplyTo().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn gốc để reply"));
            message.setReplyTo(replyTo);
        } else {
            message.setReplyTo(null);
        }

        if (message.getCreatedAt() == null) {
            message.setCreatedAt(LocalDateTime.now());
        }
        message.setEdited(false);

        // Lưu tin nhắn
        Message saved = messageRepository.save(message);

        // Tạo DTO để trả về
        MessageResponse dto = new MessageResponse(
                saved.getId(),
                saved.getConversation().getId(),
                saved.getSender().getId(),
                saved.getContent(),
                saved.getMessageType().name(),
                saved.getCreatedAt(),
                saved.getReplyTo() != null ? saved.getReplyTo().getId() : null,
                saved.isEdited()
        );

        return ApiResponse.success("00", "Gửi tin nhắn thành công", dto);
    }



    public ApiResponse<List<MessageResponse>> getMessagesByConversation(String conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            return ApiResponse.error("01", "Không tìm thấy cuộc trò chuyện với ID: " + conversationId);
        }

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        List<MessageResponse> responseList = messages.stream()
                .map(messageMapper::toMessageResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("00", "Lấy danh sách tin nhắn thành công", responseList);
    }


    // Lấy tin nhắn theo ID và conversationId
    public Optional<Message> getMessageByIdAndConversation(String id, String conversationId) {
        return messageRepository.findByIdAndConversationId(id, conversationId);
    }

    // Lấy tin nhắn của một người gửi trong một cuộc trò chuyện (theo thứ tự thời gian)
    public List<Message> getMessagesBySenderAndConversation(String senderId, String conversationId) {
        return messageRepository.findBySenderIdAndConversationIdOrderByCreatedAtAsc(senderId, conversationId);
    }

    // Chỉnh sửa tin nhắn
    public Message editMessage(String messageId, String newContent) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.setContent(newContent);  // Cập nhật nội dung tin nhắn
            return messageRepository.save(message); // Lưu lại tin nhắn đã chỉnh sửa
        } else {
            // Ném ngoại lệ hoặc trả về null nếu không tìm thấy tin nhắn
            throw new RuntimeException("Message not found with id " + messageId);
        }
    }
}
