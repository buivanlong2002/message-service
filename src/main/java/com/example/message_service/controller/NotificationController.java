package com.example.message_service.controller;

import com.example.message_service.model.Notification;
import com.example.message_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Lấy tất cả thông báo của một người dùng
    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable String userId) {
        return notificationService.getNotificationsByUser(userId);
    }

    // Lấy tất cả thông báo chưa đọc của một người dùng
    @GetMapping("/user/{userId}/unread")
    public List<Notification> getUnreadNotificationsByUser(@PathVariable String userId) {
        return notificationService.getUnreadNotificationsByUser(userId);
    }

    // Đánh dấu thông báo là đã đọc
    @PutMapping("/{notificationId}/read")
    public Notification markAsRead(@PathVariable String notificationId) {
        return notificationService.markAsRead(notificationId);
    }

    // Thêm một thông báo mới
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification addNotification(@RequestBody Notification notification) {
        return notificationService.addNotification(notification);
    }
}
