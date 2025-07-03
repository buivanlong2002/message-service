package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.AddMemberRequest;
import com.example.message_service.dto.request.GetMembersByConversationRequest; // Giả sử DTO này cũng dùng UUID
import com.example.message_service.dto.request.RemoveMemberRequest;         // Giả sử DTO này cũng dùng UUID
import com.example.message_service.model.User;
import com.example.message_service.service.ConversationMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversation-members")
public class ConversationMemberController {

    private final ConversationMemberService conversationMemberService;

    @Autowired
    public ConversationMemberController(ConversationMemberService conversationMemberService) {
        this.conversationMemberService = conversationMemberService;
    }

    /**
     * Thêm thành viên vào cuộc trò chuyện.
     * Nhận UUID từ client và chuyển đổi thành String cho tầng Service.
     */
    @PostMapping("/add")
    public ApiResponse<String> addMember(@RequestBody AddMemberRequest addMemberRequest) {
        String conversationId = addMemberRequest.getConversationId().toString();
        String userId = addMemberRequest.getUserId().toString();

        // Giả sử DTO có thể chứa role hoặc không
        String role = addMemberRequest.getRole() != null ? addMemberRequest.getRole() : "member";

        return conversationMemberService.addMemberToConversation(conversationId, userId, role);
    }

    /**
     * Lấy danh sách thành viên trong một cuộc trò chuyện.
     */
    @PostMapping("/members-by-conversation")
    public ApiResponse<List<User>> getMembersByConversation(
            @RequestBody GetMembersByConversationRequest request) {

        String conversationId = request.getConversationId().toString();

        return conversationMemberService.getMembersByConversationId(conversationId);
    }

    /**
     * Xóa thành viên khỏi cuộc trò chuyện.
     */
    @PostMapping("/remove")
    public ApiResponse<String> removeMember(@RequestBody RemoveMemberRequest removeMemberRequest) {

        String conversationId = removeMemberRequest.getConversationId().toString();
        String userId = removeMemberRequest.getUserId().toString();

        return conversationMemberService.removeMemberFromConversation(conversationId, userId);
    }
}