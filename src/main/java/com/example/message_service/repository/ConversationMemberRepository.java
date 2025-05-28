package com.example.message_service.repository;

import com.example.message_service.model.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {

    List<ConversationMember> findByConversationId(String conversationId);

    List<ConversationMember> findByUserId(String userId);

    void deleteByConversationIdAndUserId(String conversationId, String userId);
}
