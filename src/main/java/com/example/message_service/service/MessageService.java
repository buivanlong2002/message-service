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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
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

    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB

    // Gửi tin nhắn mới (kể cả tạo mới conversation nếu cần)
    public ApiResponse<MessageResponse> sendMessage(
            String senderId,
            String conversationId,
            String receiverId,
            MultipartFile[] files,
            MessageType messageType,
            String content,
            String replyToId
    ) {
        // 1. Xác thực người gửi
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người gửi"));

        // 2. Lấy hoặc tạo cuộc trò chuyện
        Conversation conversation;
        if (conversationId != null && !conversationId.isBlank()) {
            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy cuộc trò chuyện"));
        } else {
            if (receiverId == null || receiverId.isBlank()) {
                return ApiResponse.error("05", "Thiếu receiverId để tạo cuộc trò chuyện 1-1");
            }
            conversation = conversationService.getOrCreateOneToOneConversation(senderId, receiverId);
        }

        // 3. Tạo tin nhắn mới
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSender(sender);
        message.setConversation(conversation);
        message.setMessageType(messageType);
        message.setContent(content != null ? content : "");
        message.setCreatedAt(LocalDateTime.now());
        message.setEdited(false);

        // 4. Xử lý reply nếu có
        if (replyToId != null && !replyToId.isBlank()) {
            Message replyTo = messageRepository.findById(replyToId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn để trả lời"));
            message.setReplyTo(replyTo);
        }

        // 5. Xử lý file/image/video đính kèm
        List<Attachment> attachments = new ArrayList<>();
        if (files != null && files.length > 0) {
            try {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        // Phân loại file theo loại MIME
                        String contentType = file.getContentType();
                        String folder = "file"; // mặc định
                        if (contentType != null) {
                            if (contentType.startsWith("image/")) {
                                folder = "image";
                            } else if (contentType.startsWith("video/")) {
                                folder = "video";
                            }
                        }

                        // Giới hạn kích thước video
                        if ("video".equals(folder) && file.getSize() > MAX_VIDEO_SIZE) {
                            return ApiResponse.error("06", "Video quá lớn. Tối đa 100MB.");
                        }

                        // Tạo thư mục upload nếu chưa có
                        Path uploadPath = Paths.get("uploads", folder);
                        Files.createDirectories(uploadPath);

                        // Lưu file vào ổ đĩa
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        // Encode URL an toàn
                        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
                        String fileUrl = "/uploads/" + folder + "/" + encodedName;

                        // Tạo bản ghi attachment
                        Attachment attachment = new Attachment();
                        attachment.setId(UUID.randomUUID().toString());
                        attachment.setFileUrl(fileUrl);
                        attachment.setFileType(contentType);
                        attachment.setFileSize(file.getSize());
                        attachment.setMessage(message);

                        attachments.add(attachment);
                    }
                }

                if (!attachments.isEmpty()) {
                    message.setAttachments(attachments);
                }
            } catch (IOException e) {
                return ApiResponse.error("99", "Lỗi khi upload file: " + e.getMessage());
            }
        }

        // 6. Lưu tin nhắn
        Message saved = messageRepository.save(message);
        return ApiResponse.success("00", "Gửi tin nhắn thành công", messageMapper.toMessageResponse(saved));
    }



    // Lấy danh sách tin nhắn theo cuộc trò chuyện
    public ApiResponse<List<MessageResponse>> getMessagesByConversation(String conversationId, int page, int size) {
        if (!conversationRepository.existsById(conversationId)) {
            return ApiResponse.error("01", "Không tìm thấy cuộc trò chuyện với ID: " + conversationId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messagePage = messageRepository.findByConversationId(conversationId, pageable);

        List<MessageResponse> responseList = messagePage.getContent().stream()
                .map(messageMapper::toMessageResponse)
                .collect(Collectors.toList());

        Collections.reverse(responseList); // để tin mới nằm dưới

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
