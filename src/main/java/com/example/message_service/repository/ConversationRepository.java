package com.example.message_service.repository;

import com.example.message_service.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Trong ConversationRepository.java
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

    // GỢI Ý: Query để tìm cuộc trò chuyện 1-1 giữa 2 người
    @Query("SELECT c FROM Conversation c JOIN c.members m1 JOIN c.members m2 " +
            "WHERE c.isGroup = false AND m1.user.id = :userId1 AND m2.user.id = :userId2")
    Optional<Conversation> findOneToOneConversation(@Param("userId1") String userId1, @Param("userId2") String userId2);

    // GỢI Ý: Query quan trọng nhất, lấy danh sách conversation của user,
    // đồng thời fetch luôn lastMessage và thành viên để tránh N+1.
    // Sắp xếp theo updatedAt (được cập nhật mỗi khi có tin nhắn mới).
    @Query("SELECT c FROM Conversation c " +
            "JOIN c.members m " +
            "LEFT JOIN FETCH c.lastMessage lm " +
            "LEFT JOIN FETCH lm.sender " +
            "WHERE m.user.id = :userId " +
            "ORDER BY c.updatedAt DESC")
    List<Conversation> findConversationsByUserId(@Param("userId") String userId);

    // Để lấy thành viên của các cuộc trò chuyện đã lấy ở trên, ta dùng một query riêng
    // với IN clause, đây là cách hiệu quả để tránh lỗi MultipleBagFetchException.
    @Query("SELECT c FROM Conversation c JOIN FETCH c.members cm JOIN FETCH cm.user " +
            "WHERE c IN :conversations")
    List<Conversation> findConversationsWithMembers(@Param("conversations") List<Conversation> conversations);
}
