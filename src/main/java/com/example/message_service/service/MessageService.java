package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.response.MessageResponse;
import com.example.message_service.mapper.MessageMapper;
import com.example.message_service.model.*;
import com.example.message_service.repository.ConversationRepository;
import com.example.message_service.repository.MessageRepository;
import com.example.message_service.repository.UserRepository;
import com.example.message_service.service.util.FileStorageService;
import com.example.message_service.service.util.PushNewMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Dùng constructor injection, code sạch hơn @Autowired
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ConversationService conversationService;
    private final FileStorageService fileStorageService; // Service mới để xử lý file
    private final PushNewMessage pushNewMessage;
    private final MessageMapper messageMapper;

    /**
     * Gửi một tin nhắn mới.
     * Tách các tác vụ I/O và WebSocket ra chạy bất đồng bộ để trả về response ngay lập tức.
     */
    @Transactional
    public ApiResponse<MessageResponse> sendMessage(
            String senderId, String conversationId, String receiverId,
            MultipartFile[] files, MessageType messageType, String content, String replyToId
    ) {
        try {
            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người gửi: " + senderId));

            // Logic lấy hoặc tạo conversation đã được tối ưu trong ConversationService
            Conversation conversation;
            if (conversationId != null && !conversationId.isBlank()) {
                conversation = conversationRepository.findById(conversationId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy cuộc trò chuyện: " + conversationId));
            } else {
                if (receiverId == null || receiverId.isBlank()) {
                    return ApiResponse.error("05", "Thiếu receiverId để tạo cuộc trò chuyện 1-1");
                }
                conversation = conversationService.getOrCreateOneToOneConversation(senderId, receiverId);
            }

            Message message = new Message();
            // Model đã được tối ưu, không cần set ID và createdAt thủ công
            message.setSender(sender);
            message.setConversation(conversation);
            message.setMessageType(messageType);
            message.setContent(content != null ? content : "");

            if (replyToId != null && !replyToId.isBlank()) {
                Message replyTo = messageRepository.findById(replyToId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn để trả lời: " + replyToId));
                message.setReplyTo(replyTo);
            }

            // Chỉ tạo metadata cho attachment, chưa lưu file vật lý
            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        Attachment attachment = fileStorageService.createAttachmentMetadata(file, message);
                        message.addAttachment(attachment); // Dùng helper method trong Model Message
                    }
                }
            }

            // Lưu message và metadata của attachment vào DB. Bước này nhanh.
            Message savedMessage = messageRepository.save(message);

            // Cập nhật lastMessage và updatedAt cho conversation
            conversation.setLastMessage(savedMessage);
            conversationRepository.save(conversation);

            // Gọi các tác vụ chạy nền (lưu file, push socket)
            handlePostMessageTasks(savedMessage, conversation, files);

            // Trả về response cho client NGAY LẬP TỨC
            MessageResponse responseDto = messageMapper.toMessageResponse(savedMessage);
            return ApiResponse.success("00", "Gửi tin nhắn thành công", responseDto);

        } catch (Exception e) {
            // Log lỗi ở đây để debug
            System.err.println("Error in sendMessage: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("99", "Lỗi khi gửi tin nhắn: " + e.getMessage());
        }
    }

    /**
     * Phương thức này chạy trên một luồng riêng để không block luồng chính.
     */
    @Async
    public void handlePostMessageTasks(Message message, Conversation conversation, MultipartFile[] files) {
        // 1. Lưu file vật lý (chậm)
        if (files != null && files.length > 0 && !message.getAttachments().isEmpty()) {
            fileStorageService.storeFiles(files, message.getAttachments());
        }

        // 2. Push tin nhắn đến các client khác qua WebSocket
        MessageResponse responseDto = messageMapper.toMessageResponse(message);
        pushNewMessage.pushNewMessageToConversation(conversation.getId(), responseDto);

        // 3. Thông báo cho tất cả thành viên cập nhật lại danh sách cuộc trò chuyện của họ
        // Giả sử Conversation đã có sẵn danh sách members được fetch
        for (ConversationMember member : conversation.getMembers()) {
            pushNewMessage.pushUpdatedConversationsToUser(member.getUser().getId());
        }
    }

    /**
     * Lấy lịch sử tin nhắn của một cuộc trò chuyện, đã được tối ưu N+1.
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<MessageResponse>> getMessagesByConversation(String conversationId, int page, int size) {
        if (!conversationRepository.existsById(conversationId)) {
            return ApiResponse.error("01", "Không tìm thấy cuộc trò chuyện với ID: " + conversationId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Dùng phương thức query đã được tối ưu với JOIN FETCH
        Page<Message> messagePage = messageRepository.findByConversationIdWithDetails(conversationId, pageable);

        List<MessageResponse> responseList = messagePage.getContent().stream()
                .map(messageMapper::toMessageResponse)
                .collect(Collectors.toList());

        // Đảo ngược lại để hiển thị đúng thứ tự trên UI (tin nhắn cũ ở trên, mới ở dưới)
        Collections.reverse(responseList);

        return ApiResponse.success("00", "Lấy danh sách tin nhắn thành công", responseList);
    }

    /**
     * Chỉnh sửa nội dung một tin nhắn.
     */
    @Transactional
    public ApiResponse<MessageResponse> editMessage(String messageId, String newContent) {
        return messageRepository.findById(messageId)
                .map(message -> {
                    message.setContent(newContent);
                    message.setEdited(true); // @UpdateTimestamp sẽ tự cập nhật thời gian
                    Message updatedMessage = messageRepository.save(message);

                    // TODO: Gửi sự kiện message_edited qua WebSocket để các client khác cập nhật

                    MessageResponse response = messageMapper.toMessageResponse(updatedMessage);
                    return ApiResponse.success("00", "Chỉnh sửa thành công", response);
                })
                .orElse(ApiResponse.error("04", "Không tìm thấy tin nhắn để chỉnh sửa"));
    }

    // Các phương thức getMessageByIdAndConversation và getMessagesBySenderAndConversation
    // có thể giữ lại nếu bạn vẫn cần chúng cho các mục đích cụ thể khác.
    public Optional<Message> getMessageByIdAndConversation(String id, String conversationId) {
        // Cân nhắc tối ưu query này nếu được sử dụng thường xuyên
        return messageRepository.findByIdAndConversationId(id, conversationId);
    }
}