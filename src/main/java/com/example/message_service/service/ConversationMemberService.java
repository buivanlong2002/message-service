package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.model.Conversation;
import com.example.message_service.model.ConversationMember;
import com.example.message_service.model.User;
import com.example.message_service.repository.ConversationMemberRepository;
import com.example.message_service.repository.ConversationRepository;
import com.example.message_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationMemberService {
    @Autowired
    private  ConversationMemberRepository conversationMemberRepository;
    @Autowired
    private  ConversationRepository conversationRepository;
    @Autowired
    private UserRepository userRepository;



    public ApiResponse<String> addMemberToConversation(String conversationId, String userId) {
        Optional<Conversation> optionalConversation = conversationRepository.findById(conversationId);
        if (optionalConversation.isEmpty()) {
            return ApiResponse.error("02", "Không tìm thấy cuộc trò chuyện: " + conversationId);
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy người dùng: " + userId);
        }

        Conversation conversation = optionalConversation.get();
        User user = optionalUser.get();

        boolean isMember = conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId);
        if (isMember) {
            return ApiResponse.error("01", "Người dùng đã là thành viên của cuộc trò chuyện.");
        }

        ConversationMember conversationMember = new ConversationMember();
        conversationMember.setConversation(conversation);
        conversationMember.setUser(user);
        conversationMember.setJoinedAt(LocalDateTime.now());
        conversationMember.setRole("member");

        conversationMemberRepository.save(conversationMember);

        return ApiResponse.success("00", "Thêm thành viên thành công.");
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
