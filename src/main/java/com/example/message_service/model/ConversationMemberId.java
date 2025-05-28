package com.example.message_service.model;

import java.io.Serializable;
import java.util.Objects;

public class ConversationMemberId implements Serializable {

    private String conversation;
    private String user;

    public ConversationMemberId() {}

    public ConversationMemberId(String conversation, String user) {
        this.conversation = conversation;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationMemberId)) return false;
        ConversationMemberId that = (ConversationMemberId) o;
        return Objects.equals(conversation, that.conversation) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversation, user);
    }

    // getters và setters nếu cần (không bắt buộc)
}
