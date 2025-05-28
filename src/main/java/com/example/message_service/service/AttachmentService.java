package com.example.message_service.service;

import com.example.message_service.model.Attachment;
import com.example.message_service.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    // Lấy tất cả file đính kèm của một tin nhắn
    public List<Attachment> getAttachmentsByMessage(String messageId) {
        return attachmentRepository.findByMessageId(messageId);
    }

    // Thêm một file đính kèm mới
    public Attachment addAttachment(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }
}
