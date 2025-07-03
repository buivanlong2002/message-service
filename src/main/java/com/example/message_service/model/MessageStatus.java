package com.example.message_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// GỢI Ý 4: Thay thế @Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message_status")
// GỢI Ý 1: Sử dụng IdClass để định nghĩa khóa chính tổng hợp
// Index sẽ được tự động tạo trên (message_id, user_id)
@IdClass(MessageStatusId.class)
public class MessageStatus {

    // Không cần trường id riêng và @PrePersist nữa
    // @Id
    // private String id;
    // @PrePersist ...

    // GỢI Ý 1 & 4: Định nghĩa các phần của khóa chính
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    @JsonIgnore
    private Message message;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String status = "delivered"; // Ví dụ: delivered, read

    // GỢI Ý 3: Dùng annotation của Hibernate để quản lý timestamp tự động và chính xác
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}