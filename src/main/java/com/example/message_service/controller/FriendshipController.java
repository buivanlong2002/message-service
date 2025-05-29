package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.server.UID;
import java.util.UUID;

@RestController
@RequestMapping("api/friendships")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    // Gửi lời mời kết bạn
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendFriendRequest(
            @RequestParam String senderId,
            @RequestParam String receiverId) {
        ApiResponse<String> response = friendshipService.sendFriendRequest(senderId, receiverId);

        if (response.getStatus().isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    // Chấp nhận lời mời kết bạn
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<String>> acceptFriendRequest(
            @RequestParam String senderId,
            @RequestParam String receiverId) {
        ApiResponse<String> response = friendshipService.acceptFriendRequest(senderId, receiverId);
        if (response.getStatus().isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    // Từ chối lời mời kết bạn
    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<String>> rejectFriendRequest(
            @RequestParam String senderId,
            @RequestParam String receiverId) {
        ApiResponse<String> response = friendshipService.rejectFriendRequest(senderId, receiverId);

        if (response.getStatus().isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
