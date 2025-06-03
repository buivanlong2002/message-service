package com.example.message_service.repository;

import com.example.message_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    // Lấy tất cả thông báo của một người dùng
    List<Notification> findByUserId(String userId);

    // Lấy tất cả thông báo chưa đọc của một người dùng
    List<Notification> findByUserIdAndRead(String userId, boolean isRead);
}
