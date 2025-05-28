package com.example.message_service.repository;

import com.example.message_service.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
    List<Attachment> findByMessageId(String messageId); // Tìm tất cả các file đính kèm của tin nhắn
}
