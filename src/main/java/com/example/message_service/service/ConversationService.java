package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.dto.response.ConversationResponse;
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

    // Tạo nhóm trò chuyện (isGroup = true)
    public Conversation createGroupConversation(String name, String createdBy) {
        Conversation conversation = new Conversation();
        conversation.setName(name);
        conversation.setGroup(true);
        conversation.setCreatedBy(createdBy);
        conversation.setCreatedAt(LocalDateTime.now());

        Conversation saved = conversationRepository.save(conversation);
        conversationMemberService.addCreatorToConversation(saved);
        return saved;
    }

    // Tạo hoặc lấy cuộc trò chuyện 1-1
    public Conversation getOrCreateOneToOneConversation(String senderId, String receiverId) {
        Optional<Conversation> existing = findOneToOneConversation(senderId, receiverId);
        if (existing.isPresent()) return existing.get();

        Conversation conversation = new Conversation();
        conversation.setGroup(false);
        conversation.setName(null);
        conversation.setCreatedBy(senderId);
        conversation.setCreatedAt(LocalDateTime.now());

        Conversation saved = conversationRepository.save(conversation);
        conversationMemberService.addMemberToConversation(saved, senderId, "member");
        conversationMemberService.addMemberToConversation(saved, receiverId, "member");

        return saved;
    }

    // Tạo nhóm trò chuyện từ một senderId gửi đến receiverId (luôn là group)
    public Conversation createDynamicGroupFromMessage(String senderId, String receiverId) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        if (senderOpt.isEmpty()) throw new RuntimeException("Sender not found");

        String groupName = senderOpt.get().getDisplayName();
        Conversation group = new Conversation();
        group.setGroup(true);
        group.setName(groupName);
        group.setCreatedBy(senderId);
        group.setCreatedAt(LocalDateTime.now());

        Conversation saved = conversationRepository.save(group);
        conversationMemberService.addMemberToConversation(saved, senderId, "member");
        conversationMemberService.addMemberToConversation(saved, receiverId, "member");

        return saved;
    }

    public ApiResponse<ConversationResponse> updateConversation(String conversationId, UpdateConversationRequest request) {
        Optional<Conversation> optional = conversationRepository.findById(conversationId);
        if (optional.isEmpty()) {
            return ApiResponse.error("04", "Không tìm thấy cuộc trò chuyện với ID: " + conversationId);
        }

        Conversation conversation = optional.get();
        conversation.setName(request.getName());
        conversation.setGroup(request.isGroup());
        conversationRepository.save(conversation);

        ConversationResponse dto = toConversationResponse(conversation, null);
        return ApiResponse.success("00", "Cập nhật cuộc trò chuyện thành công", dto);
    }

    public void archiveConversation(String conversationId) {
        conversationRepository.findById(conversationId).ifPresent(c -> {
            c.setArchived(true);
            conversationRepository.save(c);
        });
    }

    public ApiResponse<List<ConversationResponse>> getConversationsByUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy người dùng: " + userId);
        }

        List<ConversationMember> members = conversationMemberRepository.findByUserId(userId);

        List<ConversationResponse> responses = members.stream()
                .map(ConversationMember::getConversation)
                .filter(Objects::nonNull)
                .distinct()
                .map(conv -> toConversationResponse(conv, userId))
                .collect(Collectors.toList());

        return ApiResponse.success("00", "Lấy danh sách cuộc trò chuyện thành công", responses);
    }

    private Optional<Conversation> findOneToOneConversation(String userId1, String userId2) {
        return conversationMemberRepository.findByUserId(userId1).stream()
                .map(ConversationMember::getConversation)
                .filter(conv -> !conv.isGroup())
                .filter(conv -> {
                    List<ConversationMember> members = conversationMemberRepository.findByConversationId(conv.getId());
                    return members.size() == 2 &&
                            members.stream().anyMatch(m -> m.getUser().getId().equals(userId2));
                })
                .findFirst();
    }

    private ConversationResponse toConversationResponse(Conversation conversation, String requesterId) {
        String name;
        List<ConversationMember> members = conversationMemberRepository.findByConversationId(conversation.getId());

        if (conversation.isGroup()) {
            name = conversation.getName();
        } else {
            if (requesterId == null) {
                name = members.stream()
                        .map(m -> m.getUser().getDisplayName())
                        .findFirst()
                        .orElse("Cuộc trò chuyện");
            } else {
                name = members.stream()
                        .filter(m -> !m.getUser().getId().equals(requesterId))
                        .map(m -> m.getUser().getDisplayName())
                        .findFirst()
                        .orElse("Cuộc trò chuyện");
            }
        }

        Message lastMessage = messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversation.getId());
        LastMessageInfo lastMessageInfo = null;
        if (lastMessage != null) {
            lastMessageInfo = new LastMessageInfo(
                    lastMessage.getContent(),
                    lastMessage.getSender().getDisplayName(),
                    getTimeAgo(lastMessage.getCreatedAt())
            );
        }

        return new ConversationResponse(
                conversation.getId(),
                name,
                conversation.isGroup(),
                conversation.getCreatedAt(),
                lastMessageInfo
        );
    }

    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa xong";
        if (duration.toHours() < 1) return duration.toMinutes() + " phút trước";
        if (duration.toDays() < 1) return duration.toHours() + " giờ trước";
        return duration.toDays() + " ngày trước";
    }
}
