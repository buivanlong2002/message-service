package com.example.message_service.service;

import com.example.message_service.model.Notification;
import com.example.message_service.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Lấy tất cả thông báo của một người dùng
    public List<Notification> getNotificationsByUser(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    // Lấy tất cả thông báo chưa đọc của một người dùng
    public List<Notification> getUnreadNotificationsByUser(String userId) {
        return notificationRepository.findByUserIdAndRead(userId, false);
    }

    // Đánh dấu thông báo là đã đọc
    public Notification markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    // Thêm một thông báo mới
    public Notification addNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
}
