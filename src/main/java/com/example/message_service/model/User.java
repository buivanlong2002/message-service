package com.example.message_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

// GỢI Ý 2: Thay thế @Data để tránh các vấn đề tiềm ẩn với JPA.
// @EqualsAndHashCode(of = "id") đảm bảo hai User object chỉ bằng nhau khi id giống nhau.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
// GỢI Ý 1: Thêm Index để tăng tốc độ tìm kiếm.
// Index cho email (dùng để đăng nhập), phoneNumber (dùng để tìm kiếm), và displayName.
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_phonenumber", columnList = "phoneNumber"),
        @Index(name = "idx_user_displayname", columnList = "displayName")
})
public class User implements UserDetails {

    @Id
    // GỢI Ý 3: Đánh dấu cột id là không thể cập nhật để tăng tính toàn vẹn dữ liệu.
    @Column(updatable = false)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) { // Thêm kiểm tra để tránh ghi đè id đã có
            this.id = UUID.randomUUID().toString();
        }
    }

    @Column(nullable = false)
    private String password;

    private String displayName;

    private String avatarUrl;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String email;

    private String status = "active";

    @CreationTimestamp
    // GỢI Ý 3: createdAt cũng là trường không thể cập nhật.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // --- Các phương thức của UserDetails được giữ nguyên ---

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "active".equalsIgnoreCase(this.status); // Dùng equalsIgnoreCase an toàn hơn
    }
}