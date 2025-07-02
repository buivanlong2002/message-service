package com.example.message_service.repository;

import com.example.message_service.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, String> {

    // Lấy tất cả trạng thái của một tin nhắn
    List<MessageStatus> findByMessageId(String messageId);

    // Lấy trạng thái của tin nhắn theo người dùng
    List<MessageStatus> findByMessageIdAndUserId(String messageId, String userId);

    // Lấy trạng thái của tin nhắn theo người nhận và trạng thái
    List<MessageStatus> findByUserIdAndStatus(String userId, String status);
}
