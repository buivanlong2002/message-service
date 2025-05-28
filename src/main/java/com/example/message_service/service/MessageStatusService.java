package com.example.message_service.service;

import com.example.message_service.model.MessageStatus;
import com.example.message_service.repository.MessageStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageStatusService {

    @Autowired
    private MessageStatusRepository messageStatusRepository;

    // Lấy tất cả trạng thái của một tin nhắn
    public List<MessageStatus> getStatusByMessage(String messageId) {
        return messageStatusRepository.findByMessageId(messageId);
    }

    // Lấy trạng thái của tin nhắn theo người dùng
    public List<MessageStatus> getStatusByMessageAndUser(String messageId, String userId) {
        return messageStatusRepository.findByMessageIdAndUserId(messageId, userId);
    }

    // Lấy trạng thái tin nhắn của người dùng theo trạng thái
    public List<MessageStatus> getStatusByUserAndStatus(String userId, String status) {
        return messageStatusRepository.findByUserIdAndStatus(userId, status);
    }

    // Thêm trạng thái mới cho tin nhắn
    public MessageStatus addMessageStatus(MessageStatus messageStatus) {
        return messageStatusRepository.save(messageStatus);
    }

    // Cập nhật trạng thái tin nhắn
    public MessageStatus updateMessageStatus(String messageStatusId, String newStatus) {
        MessageStatus messageStatus = messageStatusRepository.findById(messageStatusId).orElseThrow();
        messageStatus.setStatus(newStatus);
        messageStatus.setUpdatedAt(LocalDateTime.now());
        return messageStatusRepository.save(messageStatus);
    }
}
