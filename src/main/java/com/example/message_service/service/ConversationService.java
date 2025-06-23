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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationMemberRepository conversationMemberRepository; // Để truy vấn các thành viên trong cuộc trò chuyện

    @Autowired
    private UserRepository userRepository; // Để kiểm tra người dùng

    @Autowired
    private ConversationMemberService conversationMemberService;


    // Tạo cuộc trò chuyện và thêm người tạo làm thành viên (vai trò "creator")
    public Conversation createConversation(String name, boolean isGroup, String createdBy) {
        // Tạo đối tượng Conversation
        Conversation conversation = new Conversation();
        conversation.setName(name);
        conversation.setGroup(isGroup);
        conversation.setCreatedBy(createdBy);
        conversation.setCreatedAt(LocalDateTime.now());

        // Lưu cuộc trò chuyện vào DB
        Conversation savedConversation = conversationRepository.save(conversation);

        //Thêm người tạo vào danh sách thành viên nếu chưa có
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

        // Map sang DTO
        ConversationDTO dto = new ConversationDTO(
                conversation.getId(),
                conversation.getName(),
                conversation.isGroup(),
                conversation.getCreatedAt()
        );

        return ApiResponse.success("00", "Cập nhật cuộc trò chuyện thành công", dto);
    }

    // Lưu cuộc trò chuyện đã lưu trữ (archived)
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
        // 1. Kiểm tra người dùng
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy người dùng: " + userId);
        }

        // 2. Lấy các conversationMember
        List<ConversationMember> conversationMembers = conversationMemberRepository.findByUserId(userId);

        // 3. Lưu ID cuộc trò chuyện đã là thành viên (dùng Set cho hiệu năng)
        Set<String> memberConversationIds = conversationMembers.stream()
                .map(m -> m.getConversation().getId())
                .collect(Collectors.toSet());

        // 4. Danh sách nhóm người dùng là thành viên
        Set<String> addedIds = new HashSet<>();
        List<ConversationDTO> memberGroups = conversationMembers.stream()
                .map(ConversationMember::getConversation)
                .filter(Conversation::isGroup)
                .filter(c -> addedIds.add(c.getId()))
                .map(c -> new ConversationDTO(
                        c.getId(),
                        c.getName(),
                        c.isGroup(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        // 5. Danh sách nhóm do người dùng tạo nhưng chưa tham gia với tư cách member
        List<ConversationDTO> createdGroups = conversationRepository.findByCreatedBy(userId).stream()
                .filter(Conversation::isGroup)
                .filter(c -> !memberConversationIds.contains(c.getId()))
                .map(c -> new ConversationDTO(
                        c.getId(),
                        c.getName(),
                        c.isGroup(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        // 6. Gộp 2 danh sách
        memberGroups.addAll(createdGroups);

        return ApiResponse.success("00", "Lấy danh sách nhóm thành công", memberGroups);
    }


}
