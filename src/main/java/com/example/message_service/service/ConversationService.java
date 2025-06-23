package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.dto.response.ConversationResponse;
import com.example.message_service.dto.response.LastMessageInfo;
import com.example.message_service.model.*;
import com.example.message_service.repository.*;

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

        ConversationResponse dto = toConversationResponse(conversation, null, null);
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
        List<Conversation> conversations = members.stream()
                .map(ConversationMember::getConversation)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // Lấy last message cho mỗi conversation
        Map<String, Message> lastMessages = new HashMap<>();
        for (Conversation conv : conversations) {
            Message lastMsg = messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conv.getId());
            if (lastMsg != null) {
                lastMessages.put(conv.getId(), lastMsg);
            }
        }

        List<ConversationResponse> responses = conversations.stream()
                .map(conv -> toConversationResponse(conv, userId, lastMessages.get(conv.getId())))
                .sorted((a, b) -> {
                    LocalDateTime timeA = a.getLastMessage() != null ? a.getLastMessage().getCreatedAt() : a.getCreatedAt();
                    LocalDateTime timeB = b.getLastMessage() != null ? b.getLastMessage().getCreatedAt() : b.getCreatedAt();
                    return timeB.compareTo(timeA); // Mới nhất lên đầu
                })
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

    private ConversationResponse toConversationResponse(Conversation conversation, String requesterId, Message lastMessage) {
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

        LastMessageInfo lastMessageInfo = null;
        if (lastMessage != null) {
            lastMessageInfo = new LastMessageInfo(
                    lastMessage.getContent(),
                    lastMessage.getSender().getDisplayName(),
                    getTimeAgo(lastMessage.getCreatedAt()),
                    lastMessage.getCreatedAt()
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
