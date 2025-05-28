package com.example.message_service.service;

import com.example.message_service.model.Conversation;
import com.example.message_service.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // Lấy tất cả cuộc trò chuyện của người dùng
    public List<Conversation> getConversations(String userId) {
        return conversationRepository.findByCreatedBy(userId);
    }

    // Lấy cuộc trò chuyện theo ID
    public Optional<Conversation> getConversationById(String id) {
        return conversationRepository.findById(id);
    }

    // Tham gia vào cuộc trò chuyện
    public void joinConversation(String conversationId, String userId) {
        // Logic thêm người vào cuộc trò chuyện, có thể cần bảng thành viên (conversation_members)
    }

    // Lưu cuộc trò chuyện đã chỉnh sửa
    public Conversation updateConversation(String conversationId, String name, boolean isGroup) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            conversation.setName(name);
            conversation.setGroup(isGroup);
            return conversationRepository.save(conversation);
        }
        throw new RuntimeException("Conversation not found");
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
