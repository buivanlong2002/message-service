package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.model.Attachment;
import com.example.message_service.model.Message; // Import Message
import com.example.message_service.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable; // Import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import

import java.util.List;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    /**
     * Lấy tất cả file đính kèm của một tin nhắn.
     * Dữ liệu này là bất biến, rất phù hợp để cache.
     */
    // GỢI Ý 2: Áp dụng Caching.
    // "attachments" là tên vùng cache, key là messageId.
    @Cacheable(value = "attachments", key = "#messageId")
    @Transactional(readOnly = true) // GỢI Ý 3: Đánh dấu đây là transaction chỉ đọc.
    public ApiResponse<List<Attachment>> getAttachmentsByMessage(String messageId) {
        List<Attachment> attachments = attachmentRepository.findByMessageId(messageId);
        return ApiResponse.success("00", "Lấy file đính kèm thành công", attachments);
    }

    /**
     * Thêm một file đính kèm mới.
     * Phương thức này không trả về DTO mà trả về chính Entity. Điều này ổn,
     * nhưng thông thường nên có một phương thức riêng để tạo và lưu attachment.
     * Logic nghiệp vụ chính (upload file, tạo Attachment object) nên nằm ở MessageService.
     */
    @Transactional // GỢI Ý 3: Đánh dấu đây là transaction có ghi.
    public ApiResponse<Attachment> addAttachment(Attachment attachment) {
        // Giả định rằng object Attachment đã được cấu hình đúng ở nơi gọi (vd: MessageService)
        // Ví dụ: attachment.setMessage(messageObject) đã được gọi.
        Attachment savedAttachment = attachmentRepository.save(attachment);
        return ApiResponse.success("00", "Thêm file đính kèm thành công", savedAttachment);
    }

    /**
     * GỢI Ý: Một phương thức tạo attachment tường minh hơn.
     * Phương thức này sẽ được gọi từ bên trong MessageService.
     */
    @Transactional
    public Attachment createAndSaveAttachment(Message message, String fileUrl, String fileType, long fileSize, String originalFileName) {
        Attachment attachment = new Attachment();
        // Phương thức @PrePersist trong Attachment.java sẽ tự tạo ID
        attachment.setMessage(message);
        attachment.setFileUrl(fileUrl);
        attachment.setFileType(fileType);
        attachment.setFileSize(fileSize);
        attachment.setOriginalFileName(originalFileName);
        return attachmentRepository.save(attachment);
    }
}