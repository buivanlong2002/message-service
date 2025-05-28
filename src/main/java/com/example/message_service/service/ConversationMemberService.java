package com.example.message_service.service;

import com.example.message_service.model.ConversationMember;
import com.example.message_service.repository.ConversationMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationMemberService {

    private final ConversationMemberRepository conversationMemberRepository;

    @Autowired
    public ConversationMemberService(ConversationMemberRepository conversationMemberRepository) {
        this.conversationMemberRepository = conversationMemberRepository;
    }

    // Thêm thành viên vào cuộc trò chuyện
    public ConversationMember addMemberToConversation(ConversationMember conversationMember) {
        return conversationMemberRepository.save(conversationMember);
    }

    // Lấy thành viên của một cuộc trò chuyện
    public List<ConversationMember> getMembersByConversationId(String conversationId) {
        return conversationMemberRepository.findByConversationId(conversationId);
    }

    // Lấy thông tin thành viên
    public List<ConversationMember> getMembersByUserId(String userId) {
        return conversationMemberRepository.findByUserId(userId);
    }

    // Xóa thành viên khỏi cuộc trò chuyện
    public void removeMemberFromConversation(String conversationId, String userId) {
        conversationMemberRepository.deleteByConversationIdAndUserId(conversationId, userId);
    }
}
