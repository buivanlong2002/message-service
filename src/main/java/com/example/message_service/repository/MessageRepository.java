package com.example.message_service.repository;

import com.example.message_service.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    Message findTopByConversationIdOrderByCreatedAtDesc(String conversationId);

    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);  // Lấy tất cả tin nhắn theo cuộc trò chuyện

    Optional<Message> findByIdAndConversationId(String id, String conversationId);  // Lấy tin nhắn theo ID và cuộc trò chuyện

    List<Message> findBySenderIdAndConversationIdOrderByCreatedAtAsc(String senderId, String conversationId);  // Lấy tin nhắn của người gửi theo cuộc trò chuyện

    Page<Message> findByConversationId(String conversationId, Pageable pageable);

    @EntityGraph(attributePaths = {"attachments"})
    List<Message> findWithAttachmentsByConversationIdOrderByCreatedAtAsc(String conversationId);
}

