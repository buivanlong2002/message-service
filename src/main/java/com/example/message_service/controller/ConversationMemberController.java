package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.ConversationDTO;
import com.example.message_service.dto.request.AddMemberRequest;
import com.example.message_service.dto.request.GetMembersByConversationRequest;
import com.example.message_service.dto.request.GetConversationByUserRequest;
import com.example.message_service.dto.request.RemoveMemberRequest;
import com.example.message_service.model.ConversationMember;
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

    // Thêm thành viên vào cuộc trò chuyện
    @PostMapping("/add")
    public ApiResponse<String> addMember(@RequestBody AddMemberRequest addMemberRequest) {
        String uuidStringConversationId = addMemberRequest.getConversationId().toString();
        String uuidStringUserId = addMemberRequest.getUserId().toString();
        return conversationMemberService.addMemberToConversation(uuidStringConversationId, uuidStringUserId);
    }

    // Lấy danh sách thành viên trong một cuộc trò chuyện
    @PostMapping("/members-by-conversation")
    public ApiResponse<List<ConversationMember>> getMembersByConversation(
            @RequestBody GetMembersByConversationRequest request) {
        String conversationId = request.getConversationId().toString();
        return conversationMemberService.getMembersByConversationId(conversationId);
    }

    // Lấy danh sách các cuộc trò chuyện của một người dùng
    @PostMapping("/conversations-by-user")
    public ApiResponse<List<ConversationDTO>> getMembersByUser(
            @RequestBody GetConversationByUserRequest request) {
        String userId = request.getUserId().toString();
        return conversationMemberService.getConversationByUserId(userId);
    }

    // Xóa thành viên khỏi cuộc trò chuyện
    @PostMapping("/remove")
    public ApiResponse<String> removeMember(@RequestBody RemoveMemberRequest removeMemberRequest) {
        String uuidStringConversationId = removeMemberRequest.getConversationId().toString();
        String uuidStringUserId = removeMemberRequest.getUserId().toString();
        return conversationMemberService.removeMemberFromConversation(uuidStringConversationId, uuidStringUserId);
    }
}
