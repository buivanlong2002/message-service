package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.SendMessageRequest;
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.mapper.MessageMapper;
import com.example.message_service.model.*;
import com.example.message_service.repository.ConversationRepository;
import com.example.message_service.repository.MessageRepository;
import com.example.message_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ConversationService conversationService;

    // Gửi tin nhắn mới (kể cả tạo mới conversation nếu cần)
    public ApiResponse<MessageResponse> sendMessage(SendMessageRequest request) {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());

        // Lấy người gửi
        User sender = userRepository.findById(request.getSenderId().toString())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người gửi"));

        Conversation conversation;

        // Nếu có conversationId -> dùng
        if (request.getConversationId() != null) {
            conversation = conversationRepository.findById(request.getConversationId().toString())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy cuộc trò chuyện"));
        } else {
            // Nếu không có conversationId -> kiểm tra hoặc tạo mới 1-1
            if (request.getReceiverId() == null || request.getReceiverId().isBlank()) {
                return ApiResponse.error("05", "Thiếu receiverId để tạo cuộc trò chuyện 1-1");
            }
            conversation = conversationService.getOrCreateOneToOneConversation(
                    request.getSenderId().toString(), request.getReceiverId());
        }


        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setMessageType(MessageType.TEXT);
        message.setCreatedAt(LocalDateTime.now());
        message.setEdited(false);

        // Xử lý tin nhắn trả lời nếu có
        if (request.getReplyToMessageId() != null && !request.getReplyToMessageId().isBlank()) {
            Message replyTo = messageRepository.findById(request.getReplyToMessageId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn để trả lời"));
            message.setReplyTo(replyTo);
        } else {
            message.setReplyTo(null);
        }

        Message saved = messageRepository.save(message);
        MessageResponse dto = messageMapper.toMessageResponse(saved);
        return ApiResponse.success("00", "Gửi tin nhắn thành công", dto);
    }

    public ApiResponse<MessageResponse> sendMessageWithAttachment(
            String senderId,
            String conversationId,
            MultipartFile file,
            MessageType messageType,
            String content,
            String replyToId
    ) {
        // 1. Xác thực người gửi
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người gửi"));

        // 2. Xác thực cuộc trò chuyện
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy cuộc trò chuyện"));

        // 3. Tạo mới tin nhắn
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSender(sender);
        message.setConversation(conversation);
        message.setMessageType(messageType);
        message.setContent(content != null ? content : ""); // đảm bảo không null
        message.setCreatedAt(LocalDateTime.now());
        message.setEdited(false);

        // 4. Nếu có tin nhắn được reply
        if (replyToId != null && !replyToId.isBlank()) {
            Message replyTo = messageRepository.findById(replyToId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn để trả lời"));
            message.setReplyTo(replyTo);
        }

        // 5. Nếu có file đính kèm
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/file");
                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = "/file/" + fileName;

                // Tạo attachment và gắn vào message
                Attachment attachment = new Attachment();
                attachment.setId(UUID.randomUUID().toString());
                attachment.setFileUrl(fileUrl);
                attachment.setFileType(file.getContentType());
                attachment.setFileSize(file.getSize());
                attachment.setMessage(message);

                message.setAttachments(List.of(attachment));

            } catch (IOException e) {
                return ApiResponse.error("99", "Lỗi khi upload file: " + e.getMessage());
            }
        }

        // 6. Lưu message (cascade sẽ lưu cả Attachment nếu cấu hình đúng)
        Message saved = messageRepository.save(message);

        return ApiResponse.success("00", "Gửi tin nhắn có file thành công", messageMapper.toMessageResponse(saved));
    }



    // Lấy danh sách tin nhắn theo cuộc trò chuyện
    public ApiResponse<List<MessageResponse>> getMessagesByConversation(String conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            return ApiResponse.error("01", "Không tìm thấy cuộc trò chuyện với ID: " + conversationId);
        }

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        List<MessageResponse> responseList = messages.stream()
                .map(messageMapper::toMessageResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("00", "Lấy danh sách tin nhắn thành công", responseList);
    }

    // Lấy tin nhắn theo ID và conversation
    public Optional<Message> getMessageByIdAndConversation(String id, String conversationId) {
        return messageRepository.findByIdAndConversationId(id, conversationId);
    }

    // Lấy tin nhắn của một người gửi trong một cuộc trò chuyện
    public ApiResponse<List<MessageResponse>> getMessagesBySenderAndConversation(String conversationId, String senderId) {
        if (!conversationRepository.existsById(conversationId)) {
            return ApiResponse.error("01", "Không tìm thấy cuộc trò chuyện");
        }

        List<Message> messages = messageRepository.findBySenderIdAndConversationIdOrderByCreatedAtAsc(senderId, conversationId);
        List<MessageResponse> responseList = messages.stream()
                .map(messageMapper::toMessageResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("00", "Lấy tin nhắn theo người gửi thành công", responseList);
    }

    // Chỉnh sửa nội dung tin nhắn
    public ApiResponse<MessageResponse> editMessage(String messageId, String newContent) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            return ApiResponse.error("04", "Không tìm thấy tin nhắn để chỉnh sửa");
        }

        Message message = messageOpt.get();
        message.setContent(newContent);
        message.setEdited(true);

        Message updated = messageRepository.save(message);
        MessageResponse response = messageMapper.toMessageResponse(updated);
        return ApiResponse.success("00", "Chỉnh sửa thành công", response);
    }
}
