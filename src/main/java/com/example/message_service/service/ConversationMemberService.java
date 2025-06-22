package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.ConversationDTO;
import com.example.message_service.model.Conversation;
import com.example.message_service.model.ConversationMember;
import com.example.message_service.model.User;
import com.example.message_service.repository.ConversationMemberRepository;
import com.example.message_service.repository.ConversationRepository;
import com.example.message_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConversationMemberService {

    @Autowired
    private ConversationMemberRepository conversationMemberRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    // Thêm thành viên vào cuộc trò chuyện
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
        conversationMember.setId(UUID.randomUUID().toString());
        conversationMember.setConversation(conversation);
        conversationMember.setUser(user);
        conversationMember.setJoinedAt(LocalDateTime.now());
        conversationMember.setRole("member");

        conversationMemberRepository.save(conversationMember);

        return ApiResponse.success("00", "Thêm thành viên thành công.");
    }

    // Lấy danh sách thành viên của cuộc trò chuyện
    public ApiResponse<List<User>> getMembersByConversationId(String conversationId) {
        Optional<Conversation> optionalConversation = conversationRepository.findById(conversationId);
        if (optionalConversation.isEmpty()) {
            return ApiResponse.error("02", "Không tìm thấy cuộc trò chuyện: " + conversationId);
        }

        // Lấy tất cả các thành viên của cuộc trò chuyện
        List<ConversationMember> members = conversationMemberRepository.findByConversationId(conversationId);

        // Lấy người tạo nhóm
        Conversation conversation = optionalConversation.get();
        User creator = userRepository.findById(conversation.getCreatedBy()).orElse(null); // Tìm người tạo nhóm

        // Chuyển từ ConversationMember sang User và đảm bảo người tạo nhóm ở trong danh sách
        List<User> users = members.stream()
                .map(ConversationMember::getUser)  // Chuyển từ ConversationMember sang User
                .collect(Collectors.toList());

        // Nếu người tạo nhóm chưa có trong danh sách, thêm vào
        if (creator != null && !users.contains(creator)) {
            users.add(creator);
        }

        return ApiResponse.success("00", "Lấy danh sách người dùng thành công", users);
    }

    // Xóa thành viên khỏi cuộc trò chuyện
    public ApiResponse<String> removeMemberFromConversation(String conversationId, String userId) {
        Optional<Conversation> optionalConversation = conversationRepository.findById(conversationId);
        if (optionalConversation.isEmpty()) {
            return ApiResponse.error("02", "Không tìm thấy cuộc trò chuyện: " + conversationId);
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy người dùng: " + userId);
        }

        boolean isMember = conversationMemberRepository.existsByConversationIdAndUserId(conversationId, userId);
        if (!isMember) {
            return ApiResponse.error("01", "Người dùng không phải là thành viên của cuộc trò chuyện.");
        }

        conversationMemberRepository.deleteByConversationIdAndUserId(conversationId, userId);

        return ApiResponse.success("00", "Xóa thành viên thành công.");
    }
}
