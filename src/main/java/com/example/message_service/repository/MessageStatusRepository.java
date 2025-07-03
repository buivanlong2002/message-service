// Trong MessageStatusRepository.java
package com.example.message_service.repository;

import com.example.message_service.model.MessageStatus;
import com.example.message_service.model.MessageStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, MessageStatusId> {

    // GỢI Ý 3: Tối ưu N+1 query khi lấy tất cả trạng thái của một tin nhắn
    @Query("SELECT ms FROM MessageStatus ms JOIN FETCH ms.user WHERE ms.message.id = :messageId")
    List<MessageStatus> findAllByMessageIdWithUser(@Param("messageId") String messageId);

    // Phương thức này không cần thiết nữa vì đã có findById(compositeKey)
    // List<MessageStatus> findByMessageIdAndUserId(String messageId, String userId);

    // GỢI Ý 3: Tối ưu N+1 query khi lấy trạng thái theo user và status
    @Query("SELECT ms FROM MessageStatus ms JOIN FETCH ms.message WHERE ms.user.id = :userId AND ms.status = :status")
    List<MessageStatus> findAllByUserIdAndStatusWithMessage(@Param("userId") String userId, @Param("status") String status);
}