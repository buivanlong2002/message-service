// Đặt file này cùng gói với Friendship.java hoặc trong một gói con
// com/example/message_service/model/FriendshipId.java

package com.example.message_service.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data // Ở đây dùng @Data an toàn vì nó không có quan hệ lồng nhau
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipId implements Serializable {

    private String user;
    private String friend;

    // Phải override equals và hashCode cho composite key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipId that = (FriendshipId) o;
        return Objects.equals(user, that.user) && Objects.equals(friend, that.friend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, friend);
    }
}