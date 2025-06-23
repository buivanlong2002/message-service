package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.response.ConversationResponse;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.dto.response.LastMessageInfo;
import com.example.message_service.model.Conversation;
import com.example.message_service.model.ConversationMember;
import com.example.message_service.model.Message;
import com.example.message_service.model.User;
import com.example.message_service.repository.ConversationMemberRepository;
import com.example.message_service.repository.ConversationRepository;
import com.example.message_service.repository.MessageRepository;
import com.example.message_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationMemberRepository conversationMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationMemberService conversationMemberService;

    @Autowired
    private MessageRepository messageRepository;

    //Tạo cuộc trò chuyện nhóm, thêm người tạo (role: "creator")
    public Conversation createConversation(String name, boolean isGroup, String createdBy) {
        Conversation conversation = new Conversation();
        conversation.setName(name);
        conversation.setGroup(isGroup);
        conversation.setCreatedBy(createdBy);
        conversation.setCreatedAt(LocalDateTime.now());

        Conversation savedConversation = conversationRepository.save(conversation);
        conversationMemberService.addCreatorToConversation(savedConversation);
        return savedConversation;
    }


    // Thay đổi thông tin cuộc trò chuyện
    public ApiResponse<ConversationResponse> updateConversation(String conversationId, UpdateConversationRequest request) {
        Optional<Conversation> optionalConversation = conversationRepository.findById(conversationId);
        if (optionalConversation.isEmpty()) {
            return ApiResponse.error("04", "Không tìm thấy cuộc trò chuyện với ID: " + conversationId);
        }

        Conversation conversation = optionalConversation.get();
        conversation.setName(request.getName());
        conversation.setGroup(request.isGroup());

        conversationRepository.save(conversation);

        ConversationResponse dto = new ConversationResponse(
                conversation.getId(),
                conversation.getName(),
                conversation.isGroup(),
                conversation.getCreatedAt()
        );

        return ApiResponse.success("00", "Cập nhật cuộc trò chuyện thành công", dto);
    }

    // Lưu trữ cuộc trò chuyện
    public void archiveConversation(String conversationId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            conversation.setArchived(true);
            conversationRepository.save(conversation);
        } else {
            throw new RuntimeException("Conversation not found");
        }
    }

    // Lấy danh sách các nhóm từ người dùng (bao gồm nhóm người tạo và nhóm người tham gia)
    public ApiResponse<List<ConversationResponse>> getConversationsByUser(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy người dùng: " + userId);
        }

        List<ConversationMember> conversationMembers = conversationMemberRepository.findByUserId(userId);

        List<ConversationResponse> result = conversationMembers.stream()
                .map(ConversationMember::getConversation)
                .distinct()
                .map(conversation -> {
                    String name;

                    if (conversation.isGroup()) {
                        name = conversation.getName();
                    } else {
                        List<ConversationMember> members = conversationMemberRepository.findByConversationId(conversation.getId());
                        name = members.stream()
                                .filter(m -> !m.getUser().getId().equals(userId))
                                .map(m -> m.getUser().getDisplayName())
                                .findFirst()
                                .orElse("Cuộc trò chuyện");
                    }

                    // Lấy tin nhắn cuối cùng
                    Message lastMessage = messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversation.getId().toString());
                    LastMessageInfo lastMessageInfo = null;

                    if (lastMessage != null) {
                        String senderName = lastMessage.getSender().getDisplayName();
                        String timeAgo = getTimeAgo(lastMessage.getCreatedAt());

                        lastMessageInfo = new LastMessageInfo(
                                lastMessage.getContent(),
                                senderName,
                                timeAgo
                        );
                    }

                    return new ConversationResponse(
                            conversation.getId(),
                            name,
                            conversation.isGroup(),
                            conversation.getCreatedAt(),
                            lastMessageInfo
                    );
                })
                .collect(Collectors.toList());

        return ApiResponse.success("00", "Lấy danh sách cuộc trò chuyện thành công", result);
    }


    // Tìm cuộc trò chuyện 1-1 giữa hai người
    public Optional<Conversation> findOneToOneConversation(String userId1, String userId2) {
        List<ConversationMember> membersUser1 = conversationMemberRepository.findByUserId(userId1);

        for (ConversationMember member : membersUser1) {
            Conversation conversation = member.getConversation();
            if (!conversation.isGroup()) {
                List<ConversationMember> members = conversationMemberRepository.findByConversationId(conversation.getId());
                if (members.size() == 2 &&
                        members.stream().anyMatch(m -> m.getUser().getId().equals(userId2))) {
                    return Optional.of(conversation);
                }
            }
        }

        return Optional.empty();
    }

    // Tạo cuộc trò chuyện 1-1 nếu chưa có
    public Conversation getOrCreateOneToOneConversation(String userId1, String userId2) {
        Optional<Conversation> existing = findOneToOneConversation(userId1, userId2);
        if (existing.isPresent()) {
            return existing.get();
        }

        Conversation conversation = new Conversation();
        conversation.setGroup(false);
        conversation.setName(null); // 1-1 không cần tên
        conversation.setCreatedBy(userId1);
        conversation.setCreatedAt(LocalDateTime.now());

        Conversation saved = conversationRepository.save(conversation);

        conversationMemberService.addMemberToConversation(saved, userId1, "member");
        conversationMemberService.addMemberToConversation(saved, userId2, "member");

        return saved;
    }

    private String getTimeAgo(LocalDateTime time) {
        Duration duration = Duration.between(time, LocalDateTime.now());

        if (duration.toMinutes() < 1) return "Vừa xong";
        if (duration.toHours() < 1) return duration.toMinutes() + " phút trước";
        if (duration.toDays() < 1) return duration.toHours() + " giờ trước";
        return duration.toDays() + " ngày trước";
    }

}
