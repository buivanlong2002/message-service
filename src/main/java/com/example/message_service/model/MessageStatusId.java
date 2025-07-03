// Đặt file này cùng gói với MessageStatus.java
// com/example/message_service/model/MessageStatusId.java

package com.example.message_service.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusId implements Serializable {

    private String message; // Tên trường phải khớp với tên trường trong MessageStatus
    private String user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageStatusId that = (MessageStatusId) o;
        return Objects.equals(message, that.message) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, user);
    }
}