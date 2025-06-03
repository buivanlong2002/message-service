package com.example.message_service.repository;

import com.example.message_service.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, String> {

    // Tìm tất cả các cuộc trò chuyện của người dùng
    List<Conversation> findByCreatedBy(String createdBy);
}
