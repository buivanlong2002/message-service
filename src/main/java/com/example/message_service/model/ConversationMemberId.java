// Đặt file này cùng gói với ConversationMember.java
// com/example/message_service/model/ConversationMemberId.java

package com.example.message_service.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data // An toàn khi dùng ở đây
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMemberId implements Serializable {

    private String conversation;
    private String user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversationMemberId that = (ConversationMemberId) o;
        return Objects.equals(conversation, that.conversation) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversation, user);
    }
}