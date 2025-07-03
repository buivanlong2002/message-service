package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.dto.response.ConversationResponse;
import com.example.message_service.dto.response.LastMessageInfo;
import com.example.message_service.model.*;
import com.example.message_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.message_service.dto.request.CreateConversationRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Sử dụng constructor injection, không cần @Autowired
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;
    // Xóa dependency vòng với ConversationMemberService nếu có thể

    // ----------------- CREATE --------------------
    @Transactional
// Khi một cuộc trò chuyện mới được tạo, cache danh sách của tất cả các thành viên liên quan phải được xóa.
    @CacheEvict(value = "userConversations", allEntries = true) // allEntries = true là cách đơn giản nhất
    public ApiResponse<ConversationResponse> createConversation(CreateConversationRequest request) {
        try {
            Conversation conversation;
            if (request.isGroup()) {
                // Logic tạo nhóm
                if (request.getName() == null || request.getName().isBlank()) {
                    return ApiResponse.error("11", "Tên nhóm là bắt buộc");
                }
                conversation = createGroup(request);
            } else {
                // Logic tạo chat 1-1
                if (request.getMemberIds() == null || request.getMemberIds().size() != 2) {
                    return ApiResponse.error("12", "Chat 1-1 yêu cầu chính xác 2 thành viên");
                }
                // Gọi lại phương thức getOrCreate để tận dụng logic tìm kiếm
                String userId1 = request.getMemberIds().get(0);
                String userId2 = request.getMemberIds().get(1);
                conversation = getOrCreateOneToOneConversation(userId1, userId2);
            }

            // Chuyển đổi entity đã lưu sang DTO để trả về
            ConversationResponse responseDto = toConversationResponse(conversation, request.getCreatedBy());
            return ApiResponse.success("00", "Tạo cuộc trò chuyện thành công", responseDto);

        } catch (Exception e) {
            // Log lỗi ở đây
            return ApiResponse.error("99", "Đã có lỗi xảy ra khi tạo cuộc trò chuyện: " + e.getMessage());
        }
    }


    /**
     * Helper method để xử lý logic tạo nhóm.
     */
    private Conversation createGroup(CreateConversationRequest request) {
        Conversation conversation = new Conversation();
        conversation.setGroup(true);
        conversation.setName(request.getName());
        conversation.setCreatedBy(request.getCreatedBy());

        // Lấy danh sách User object từ memberIds
        List<User> members = userRepository.findAllById(request.getMemberIds());
        if (members.size() != request.getMemberIds().size()) {
            throw new RuntimeException("Một hoặc nhiều user ID không hợp lệ.");
        }

        // Tạo các ConversationMember object
        List<ConversationMember> conversationMembers = members.stream().map(user -> {
            ConversationMember member = new ConversationMember();
            member.setConversation(conversation);
            member.setUser(user);
            // Gán vai trò 'creator' cho người tạo, còn lại là 'member'
            String role = user.getId().equals(request.getCreatedBy()) ? "creator" : "member";
            member.setRole(role);
            return member;
        }).collect(Collectors.toList());

        conversation.setMembers(conversationMembers);
        return conversationRepository.save(conversation);
    }

    @Transactional
    public Conversation getOrCreateOneToOneConversation(String userId1, String userId2) {
        // Dùng query đã tối ưu
        return conversationRepository.findOneToOneConversation(userId1, userId2)
                .orElseGet(() -> {
                    User user1 = userRepository.findById(userId1).orElseThrow();
                    User user2 = userRepository.findById(userId2).orElseThrow();

                    Conversation conversation = new Conversation();
                    conversation.setGroup(false);
                    conversation.setCreatedBy(userId1);

                    ConversationMember member1 = new ConversationMember();
                    member1.setConversation(conversation);
                    member1.setUser(user1);
                    member1.setRole("member");

                    ConversationMember member2 = new ConversationMember();
                    member2.setConversation(conversation);
                    member2.setUser(user2);
                    member2.setRole("member");

                    conversation.getMembers().add(member1);
                    conversation.getMembers().add(member2);

                    return conversationRepository.save(conversation);
                });
    }

    // ----------------- UPDATE --------------------
    @Transactional
    @CacheEvict(value = "userConversations", allEntries = true) // Xóa cache khi có thay đổi
    public ApiResponse<ConversationResponse> updateConversation(String conversationId, UpdateConversationRequest request) {
        return conversationRepository.findById(conversationId)
                .map(conversation -> {
                    conversation.setName(request.getName());
                    // Logic update khác nếu cần
                    Conversation updatedConv = conversationRepository.save(conversation);
                    ConversationResponse dto = toConversationResponse(updatedConv, updatedConv.getCreatedBy());
                    return ApiResponse.success("00", "Cập nhật thành công", dto);
                })
                .orElse(ApiResponse.error("04", "Không tìm thấy cuộc trò chuyện"));
    }

    // ----------------- GET --------------------
    @Transactional(readOnly = true)
    @Cacheable(value = "userConversations", key = "#userId")
    public ApiResponse<List<ConversationResponse>> getConversationsByUser(String userId) {
        if (!userRepository.existsById(userId)) {
            return ApiResponse.error("03", "Không tìm thấy người dùng: " + userId);
        }

        // Bước 1: Lấy danh sách conversation và lastMessage trong 1 query
        List<Conversation> conversations = conversationRepository.findConversationsByUserId(userId);
        if (conversations.isEmpty()) {
            return ApiResponse.success("00", "Không có cuộc trò chuyện nào", List.of());
        }

        // Bước 2: Lấy tất cả thành viên của các conversation trên trong 1 query
        List<Conversation> conversationsWithMembers = conversationRepository.findConversationsWithMembers(conversations);

        // Tạo một Map để dễ dàng tra cứu conversation đã có đủ thành viên
        Map<String, Conversation> convMap = conversationsWithMembers.stream()
                .collect(Collectors.toMap(Conversation::getId, Function.identity()));

        // Bước 3: Chuyển đổi sang DTO (không có query DB nào ở đây)
        List<ConversationResponse> responses = conversations.stream()
                .map(conv -> {
                    Conversation fullConv = convMap.get(conv.getId());
                    return toConversationResponse(fullConv, userId);
                })
                .collect(Collectors.toList());

        return ApiResponse.success("00", "Lấy danh sách cuộc trò chuyện thành công", responses);
    }

    // ----------------- HELPER --------------------

    // Helper đã được tối ưu, không cần tham số lastMessage
    private ConversationResponse toConversationResponse(Conversation conversation, String requesterId) {
        String name;
        String avatarUrl;
        Message lastMessage = conversation.getLastMessage(); // Lấy từ object đã được fetch

        if (conversation.isGroup()) {
            name = conversation.getName();
            avatarUrl = conversation.getAvatarUrl();
        } else {
            // Tìm đối tác chat từ danh sách thành viên đã được fetch
            User partner = conversation.getMembers().stream()
                    .map(ConversationMember::getUser)
                    .filter(user -> !user.getId().equals(requesterId))
                    .findFirst()
                    .orElse(null); // Hoặc ném lỗi nếu không tìm thấy

            name = (partner != null) ? partner.getDisplayName() : "Cuộc trò chuyện đã xóa";
            avatarUrl = (partner != null) ? partner.getAvatarUrl() : null;
        }

        LastMessageInfo lastMessageInfo = null;
        if (lastMessage != null) {
            lastMessageInfo = new LastMessageInfo(
                    lastMessage.getContent(),
                    lastMessage.getSender() != null ? lastMessage.getSender().getDisplayName() : "Người dùng đã xóa",
                    getTimeAgo(lastMessage.getCreatedAt()),
                    lastMessage.getCreatedAt()
            );
        }

        return new ConversationResponse(
                conversation.getId(),
                name,
                conversation.isGroup(),
                avatarUrl,
                conversation.getUpdatedAt(), // Sắp xếp theo updatedAt
                lastMessageInfo
        );
    }

    // getTimeAgo giữ nguyên
    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa xong";
        if (duration.toHours() < 1) return duration.toMinutes() + " phút trước";
        if (duration.toDays() < 1) return duration.toHours() + " giờ trước";
        return duration.toDays() + " ngày trước";
    }
}