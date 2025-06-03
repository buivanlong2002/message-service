package com.example.message_service.repository;

import com.example.message_service.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);  // Lấy tất cả tin nhắn theo cuộc trò chuyện

    Optional<Message> findByIdAndConversationId(String id, String conversationId);  // Lấy tin nhắn theo ID và cuộc trò chuyện

    List<Message> findBySenderIdAndConversationIdOrderByCreatedAtAsc(String senderId, String conversationId);  // Lấy tin nhắn của người gửi theo cuộc trò chuyện
}

