package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.ConversationDTO;
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.model.Conversation;
import com.example.message_service.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    // Tạo cuộc trò chuyện
    public Conversation createConversation(String name, boolean isGroup, String createdBy) {
        Conversation conversation = new Conversation();
        conversation.setName(name);
        conversation.setGroup(isGroup);
        conversation.setCreatedBy(createdBy);
        conversation.setCreatedAt(LocalDateTime.now());

        return conversationRepository.save(conversation);
    }

//    // Lấy tất cả cuộc trò chuyện của người dùng
//    public List<Conversation> getConversations(String userId) {
//        return conversationRepository.findByCreatedBy(userId);
//    }
//
//    // Lấy cuộc trò chuyện theo ID
//    public Optional<Conversation> getConversationById(String id) {
//        return conversationRepository.findById(id);
//    }
//
//    // Tham gia vào cuộc trò chuyện
//    public void joinConversation(String conversationId, String userId) {
//        // Logic thêm người vào cuộc trò chuyện, có thể cần bảng thành viên (conversation_members)
//    }

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
}
