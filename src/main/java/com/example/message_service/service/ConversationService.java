package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.ConversationDTO;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.model.Conversation;
import com.example.message_service.model.ConversationMember;
import com.example.message_service.model.User;
import com.example.message_service.repository.ConversationMemberRepository;
import com.example.message_service.repository.ConversationRepository;
import com.example.message_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public ApiResponse<ConversationDTO> updateConversation(String conversationId, UpdateConversationRequest request) {
        Optional<Conversation> optionalConversation = conversationRepository.findById(conversationId);
        if (optionalConversation.isEmpty()) {
            return ApiResponse.error("04", "Không tìm thấy cuộc trò chuyện với ID: " + conversationId);
        }

        Conversation conversation = optionalConversation.get();
        conversation.setName(request.getName());
        conversation.setGroup(request.isGroup());

        conversationRepository.save(conversation);

        ConversationDTO dto = new ConversationDTO(
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
    public ApiResponse<List<ConversationDTO>> getConversationsByUser(String userId) {
        // 1. Kiểm tra người dùng có tồn tại
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy người dùng: " + userId);
        }

        // 2. Lấy tất cả các cuộc trò chuyện người này là thành viên
        List<ConversationMember> conversationMembers = conversationMemberRepository.findByUserId(userId);

        // 3. Map sang DTO
        List<ConversationDTO> result = conversationMembers.stream()
                .map(ConversationMember::getConversation)
                .distinct()
                .map(conversation -> {
                    String name;

                    if (conversation.isGroup()) {
                        // Tên nhóm giữ nguyên
                        name = conversation.getName();
                    } else {
                        // Với 1-1, lấy tên người còn lại
                        List<ConversationMember> members = conversationMemberRepository.findByConversationId(conversation.getId());

                        name = members.stream()
                                .filter(m -> !m.getUser().getId().equals(userId)) // người còn lại
                                .map(m -> m.getUser().getDisplayName())
                                .findFirst()
                                .orElse("Cuộc trò chuyện");
                    }

                    return new ConversationDTO(
                            conversation.getId(),
                            name,
                            conversation.isGroup(),
                            conversation.getCreatedAt()
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
}
