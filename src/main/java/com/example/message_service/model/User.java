package com.example.message_service.model;
<<<<<<< HEAD
=======

>>>>>>> 996ff0311a5d38304fa620dea285c633ff4f142b
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Id
<<<<<<< HEAD
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
=======
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
>>>>>>> 996ff0311a5d38304fa620dea285c633ff4f142b

    @Column(unique = true, nullable = false)
    private String username;

<<<<<<< HEAD
    private String passwordHash;

=======
>>>>>>> 996ff0311a5d38304fa620dea285c633ff4f142b
    private String password;

    private String displayName;

    private String avatarUrl;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private String status = "active";

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
