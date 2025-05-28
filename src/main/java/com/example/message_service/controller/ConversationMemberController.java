package com.example.message_service.controller;

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
    @PostMapping
    public ConversationMember addMember(@RequestBody ConversationMember conversationMember) {
        return conversationMemberService.addMemberToConversation(conversationMember);
    }

    // Lấy danh sách thành viên của cuộc trò chuyện
    @GetMapping("/conversation/{conversationId}")
    public List<ConversationMember> getMembersByConversation(@PathVariable String conversationId) {
        return conversationMemberService.getMembersByConversationId(conversationId);
    }

    // Lấy danh sách các cuộc trò chuyện của người dùng
    @GetMapping("/user/{userId}")
    public List<ConversationMember> getMembersByUser(@PathVariable String userId) {
        return conversationMemberService.getMembersByUserId(userId);
    }

    // Xóa thành viên khỏi cuộc trò chuyện
    @DeleteMapping("/remove")
    public void removeMember(@RequestParam String conversationId, @RequestParam String userId) {
        conversationMemberService.removeMemberFromConversation(conversationId, userId);
    }
}
