package com.example.message_service.repository;

import com.example.message_service.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Import nếu chưa có

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    // Query đã tối ưu để lấy lịch sử chat
    @Query(value = "SELECT m FROM Message m " +
            "LEFT JOIN FETCH m.sender " +
            "LEFT JOIN FETCH m.replyTo " +
            "LEFT JOIN FETCH m.attachments " +
            "WHERE m.conversation.id = :conversationId",
            countQuery = "SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId")
    Page<Message> findByConversationIdWithDetails(@Param("conversationId") String conversationId, Pageable pageable);

    // BỔ SUNG PHƯƠNG THỨC BỊ THIẾU Ở ĐÂY
    /**
     * Tìm một tin nhắn theo ID của nó và ID của cuộc trò chuyện mà nó thuộc về.
     * Điều này giúp đảm bảo rằng bạn chỉ lấy được tin nhắn từ đúng cuộc trò chuyện.
     */
    Optional<Message> findByIdAndConversationId(String id, String conversationId);

}