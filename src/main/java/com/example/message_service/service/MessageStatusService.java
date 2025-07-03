package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.model.Message;
import com.example.message_service.model.MessageStatus;
import com.example.message_service.model.MessageStatusId;
import com.example.message_service.model.User;
import com.example.message_service.repository.MessageRepository; // Import
import com.example.message_service.repository.MessageStatusRepository;
import com.example.message_service.repository.UserRepository; // Import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageStatusService {

    private final MessageStatusRepository messageStatusRepository;
    private final MessageRepository messageRepository; // Thêm
    private final UserRepository userRepository;         // Thêm

    @Autowired
    public MessageStatusService(MessageStatusRepository messageStatusRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.messageStatusRepository = messageStatusRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lấy tất cả trạng thái của một tin nhắn (ví dụ: tất cả những người đã xem).
     */
    @Cacheable(value = "messageStatuses", key = "#messageId")
    @Transactional(readOnly = true)
    public ApiResponse<List<MessageStatus>> getStatusesByMessage(String messageId) {
        // Dùng query đã tối ưu N+1
        List<MessageStatus> statuses = messageStatusRepository.findAllByMessageIdWithUser(messageId);
        if (statuses.isEmpty()) {
            return ApiResponse.error("01", "Không có trạng thái nào cho tin nhắn này");
        }
        return ApiResponse.success("00", "Lấy trạng thái theo tin nhắn thành công", statuses);
    }

    /**
     * Cập nhật hoặc tạo mới trạng thái cho một người dùng với một tin nhắn.
     * Ví dụ: Đánh dấu là "read".
     */
    @Caching(evict = {
            @CacheEvict(value = "messageStatuses", key = "#messageId")
    })
    @Transactional
    public ApiResponse<MessageStatus> upsertMessageStatus(String messageId, String userId, String newStatus) {
        MessageStatusId statusId = new MessageStatusId(messageId, userId);

        // Tìm kiếm xem đã có status cho cặp (message, user) này chưa
        MessageStatus status = messageStatusRepository.findById(statusId)
                .orElseGet(() -> {
                    // Nếu chưa có, tạo mới
                    Message message = messageRepository.findById(messageId).orElse(null);
                    User user = userRepository.findById(userId).orElse(null);
                    if (message == null || user == null) {
                        return null; // Sẽ được xử lý ở dưới
                    }
                    MessageStatus newMs = new MessageStatus();
                    newMs.setMessage(message);
                    newMs.setUser(user);
                    return newMs;
                });

        if (status == null) {
            return ApiResponse.error("04", "Không tìm thấy Message hoặc User");
        }

        // Cập nhật trạng thái và lưu lại.
        // @UpdateTimestamp sẽ tự động cập nhật updatedAt.
        status.setStatus(newStatus);
        MessageStatus savedStatus = messageStatusRepository.save(status);

        return ApiResponse.success("00", "Cập nhật/Tạo trạng thái thành công", savedStatus);
    }

    /*
    Lưu ý:
    - Các phương thức getStatusByMessageAndUser, getStatusByUserAndStatus, addMessageStatus, updateMessageStatus
      đã được hợp nhất và thay thế bằng một phương thức `upsertMessageStatus` mạnh mẽ và logic hơn.
    - `upsert` (update or insert) là một pattern phổ biến. Khi client báo "user A đã xem message B",
      bạn không cần biết trước đó đã có bản ghi "delivered" hay chưa, bạn chỉ cần đảm bảo có một bản ghi
      với trạng thái là "read". Phương thức `upsert` xử lý cả hai trường hợp đó.
    */
}