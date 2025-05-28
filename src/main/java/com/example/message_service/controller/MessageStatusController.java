package com.example.message_service.controller;

import com.example.message_service.model.MessageStatus;
import com.example.message_service.service.MessageStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message-status")
public class MessageStatusController {

    @Autowired
    private MessageStatusService messageStatusService;

    // Lấy tất cả trạng thái của một tin nhắn
    @GetMapping("/message/{messageId}")
    public List<MessageStatus> getStatusByMessage(@PathVariable String messageId) {
        return messageStatusService.getStatusByMessage(messageId);
    }

    // Lấy trạng thái của tin nhắn theo người dùng
    @GetMapping("/message/{messageId}/user/{userId}")
    public List<MessageStatus> getStatusByMessageAndUser(@PathVariable String messageId, @PathVariable String userId) {
        return messageStatusService.getStatusByMessageAndUser(messageId, userId);
    }

    // Lấy trạng thái của tin nhắn theo người dùng và trạng thái
    @GetMapping("/user/{userId}/status/{status}")
    public List<MessageStatus> getStatusByUserAndStatus(@PathVariable String userId, @PathVariable String status) {
        return messageStatusService.getStatusByUserAndStatus(userId, status);
    }

    // Thêm trạng thái mới cho tin nhắn
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageStatus addMessageStatus(@RequestBody MessageStatus messageStatus) {
        return messageStatusService.addMessageStatus(messageStatus);
    }

    // Cập nhật trạng thái tin nhắn
    @PutMapping("/{messageStatusId}/status/{newStatus}")
    public MessageStatus updateMessageStatus(@PathVariable String messageStatusId, @PathVariable String newStatus) {
        return messageStatusService.updateMessageStatus(messageStatusId, newStatus);
    }
}
