package com.example.message_service.controller;

import com.example.message_service.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    // Gửi lời mời kết bạn
    @PostMapping("/send")
    public String sendFriendRequest(@RequestParam UUID senderId, @RequestParam UUID receiverId) {
        boolean success = friendshipService.sendFriendRequest(senderId, receiverId);
        return success ? "Friend request sent" : "Friend request already exists or failed";
    }

    // Chấp nhận lời mời kết bạn
    @PostMapping("/accept")
    public String acceptFriendRequest(@RequestParam UUID senderId, @RequestParam UUID receiverId) {
        boolean success = friendshipService.acceptFriendRequest(senderId, receiverId);
        return success ? "Friend request accepted" : "Failed to accept friend request";
    }

    // Từ chối lời mời kết bạn
    @PostMapping("/reject")
    public String rejectFriendRequest(@RequestParam UUID senderId, @RequestParam UUID receiverId) {
        boolean success = friendshipService.rejectFriendRequest(senderId, receiverId);
        return success ? "Friend request rejected" : "Failed to reject friend request";
    }
}
