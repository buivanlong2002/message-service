package com.example.message_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_members")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMember {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", referencedColumnName = "id")
    private Conversation conversation;


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'member'")
    private String role = "member";


}
