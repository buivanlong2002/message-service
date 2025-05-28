package com.example.message_service.controller;

import com.example.message_service.model.Attachment;
import com.example.message_service.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    // Lấy tất cả file đính kèm của một tin nhắn
    @GetMapping("/message/{messageId}")
    public List<Attachment> getAttachmentsByMessage(@PathVariable String messageId) {
        return attachmentService.getAttachmentsByMessage(messageId);
    }

    // Thêm một file đính kèm
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Attachment addAttachment(@RequestBody Attachment attachment) {
        return attachmentService.addAttachment(attachment);
    }
}
