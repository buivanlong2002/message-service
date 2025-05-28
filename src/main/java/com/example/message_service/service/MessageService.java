package com.example.message_service.service;

import com.example.message_service.model.Message;
import com.example.message_service.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // Gửi tin nhắn mới
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    // Lấy tất cả tin nhắn trong một cuộc trò chuyện (theo thứ tự thời gian)
    public List<Message> getMessagesByConversation(String conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    // Lấy tin nhắn theo ID và conversationId
    public Optional<Message> getMessageByIdAndConversation(String id, String conversationId) {
        return messageRepository.findByIdAndConversationId(id, conversationId);
    }

    // Lấy tin nhắn của một người gửi trong một cuộc trò chuyện (theo thứ tự thời gian)
    public List<Message> getMessagesBySenderAndConversation(String senderId, String conversationId) {
        return messageRepository.findBySenderIdAndConversationIdOrderByCreatedAtAsc(senderId, conversationId);
    }

    // Chỉnh sửa tin nhắn
    public Message editMessage(String messageId, String newContent) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.setContent(newContent);  // Cập nhật nội dung tin nhắn
            return messageRepository.save(message); // Lưu lại tin nhắn đã chỉnh sửa
        } else {
            // Ném ngoại lệ hoặc trả về null nếu không tìm thấy tin nhắn
            throw new RuntimeException("Message not found with id " + messageId);
        }
    }
}
