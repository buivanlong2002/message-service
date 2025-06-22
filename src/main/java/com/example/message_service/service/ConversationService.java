package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.ConversationDTO;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.model.Conversation;
import com.example.message_service.model.User;
import com.example.message_service.repository.ConversationRepository;
import com.example.message_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationMemberService conversationMemberService; // Inject ConversationMemberService để lấy nhóm của người dùng

    @Autowired
    private UserRepository userRepository; // Inject UserRepository nếu bạn muốn kiểm tra người dùng trước khi lấy thông tin

    // Tạo cuộc trò chuyện
    public Conversation createConversation(String name, boolean isGroup, String createdBy) {
        Conversation conversation = new Conversation();
        conversation.setName(name);
        conversation.setGroup(isGroup);
        conversation.setCreatedBy(createdBy);
        conversation.setCreatedAt(LocalDateTime.now());

        return conversationRepository.save(conversation);
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
        // Kiểm tra người dùng có tồn tại không
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy người dùng: " + userId);
        }

        // Lấy các cuộc trò chuyện mà người dùng tham gia
        List<ConversationDTO> conversationList = conversationMemberService.getConversationByUserId(userId).getData();

        // Lấy các cuộc trò chuyện mà người dùng là người tạo
        List<Conversation> createdConversations = conversationRepository.findByCreatedBy(userId);

        // Kết hợp cả hai danh sách
        createdConversations.forEach(conversation -> {
            // Nếu người dùng là người tạo cuộc trò chuyện, thêm vào danh sách
            conversationList.add(new ConversationDTO(
                    conversation.getId(),
                    conversation.getName(),
                    conversation.isGroup(),
                    conversation.getCreatedAt()
            ));
        });

        return ApiResponse.success("00", "Lấy danh sách các cuộc trò chuyện thành công", conversationList);
    }
}
