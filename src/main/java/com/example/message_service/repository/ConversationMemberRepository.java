// Trong ConversationMemberRepository.java
package com.example.message_service.repository;

import com.example.message_service.model.ConversationMember;
import com.example.message_service.model.ConversationMemberId;
import com.example.message_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Sửa lại JpaRepository để làm việc với Composite Key
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, ConversationMemberId> {

    // Không cần existsBy... nữa vì findById(compositeKey) đã làm việc đó hiệu quả hơn.

    // GỢI Ý 4: Tối ưu N+1 Query
    // Query này lấy cả thông tin User trong một lần, tránh N+1 query.
    @Query("SELECT cm FROM ConversationMember cm JOIN FETCH cm.user WHERE cm.conversation.id = :conversationId")
    List<ConversationMember> findByConversationIdWithUser(@Param("conversationId") String conversationId);

    // Phương thức cũ này vẫn có thể dùng, nhưng sẽ gây ra N+1.
    // List<ConversationMember> findByConversationId(String conversationId);

    // Phương thức để lấy một thành viên cụ thể
    Optional<ConversationMember> findByConversationIdAndUserId(String conversationId, String userId);

}