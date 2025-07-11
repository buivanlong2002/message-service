package com.example.message_service.repository;

import com.example.message_service.model.MessageStatus;
import com.example.message_service.model.MessageStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, String> {

    List<MessageStatus> findByMessageId(String messageId);

    MessageStatus findByMessageIdAndUserId(String messageId, String userId);

    List<MessageStatus> findByMessageIdInAndStatus(Collection<String> message_id, MessageStatusEnum status);




    List<MessageStatus> findByUserIdAndStatus(String userId, String status);
}
