package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.response.FriendRequestResponse;
import com.example.message_service.model.User;
import com.example.message_service.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users/{userId}")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    /**
     * Lấy danh sách bạn bè của một user.
     * GET /api/users/{userId}/friends
     */
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<List<User>>> listFriends(@PathVariable String userId) {
        ApiResponse<List<User>> response = friendshipService.getFriendships(userId);
        // buildResponse sẽ xử lý việc trả về OK hay không.
        return buildResponse(response);
    }

    /**
     * Lấy danh sách lời mời kết bạn đang chờ của một user.
     * GET /api/users/{userId}/friend-requests/pending
     */
    @GetMapping("/friend-requests/pending")
    public ResponseEntity<ApiResponse<List<FriendRequestResponse>>> getPendingFriendRequests(@PathVariable String userId) {
        ApiResponse<List<FriendRequestResponse>> response = friendshipService.getPendingRequests(userId);
        return buildResponse(response);
    }

    // ----- Các API để quản lý lời mời kết bạn -----

    /**
     * User hiện tại (userId) gửi lời mời kết bạn đến một người khác (receiverId).
     * POST /api/users/{userId}/friend-requests
     * Body: { "receiverId": "some-uuid" }
     */
    @PostMapping("/friend-requests")
    public ResponseEntity<ApiResponse<String>> sendFriendRequest(
            @PathVariable("userId") String senderId,
            @RequestBody Map<String, String> payload) {
        String receiverId = payload.get("receiverId");
        if (receiverId == null || receiverId.isBlank()) {
            return buildResponse(ApiResponse.error("10", "receiverId là bắt buộc")); // Lỗi validation
        }
        ApiResponse<String> response = friendshipService.sendFriendRequest(senderId, receiverId);
        return buildResponse(response);
    }

    /**
     * User hiện tại (userId) chấp nhận lời mời từ một người khác (senderId).
     * PUT /api/users/{userId}/friend-requests/{senderId}/accept
     */
    @PutMapping("/friend-requests/{senderId}/accept")
    public ResponseEntity<ApiResponse<String>> acceptFriendRequest(
            @PathVariable("userId") String receiverId,
            @PathVariable String senderId) {
        ApiResponse<String> response = friendshipService.acceptFriendRequest(senderId, receiverId);
        return buildResponse(response);
    }

    /**
     * User hiện tại (userId) từ chối lời mời từ một người khác (senderId).
     * DELETE /api/users/{userId}/friend-requests/{senderId}
     */
    @DeleteMapping("/friend-requests/{senderId}")
    public ResponseEntity<ApiResponse<String>> rejectFriendRequest(
            @PathVariable("userId") String receiverId,
            @PathVariable String senderId) {
        ApiResponse<String> response = friendshipService.rejectFriendRequest(senderId, receiverId);
        return buildResponse(response);
    }

    /**
     * Phương thức helper để xây dựng ResponseEntity từ ApiResponse.
     * Phương thức này có thể tái sử dụng cho mọi loại ApiResponse nhờ Generics.
     * @param response ApiResponse nhận được từ tầng service.
     * @return ResponseEntity với status code phù hợp.
     * @param <T> Kiểu dữ liệu của payload.
     */
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(ApiResponse<T> response) {
        boolean isSuccess = response.getStatus().isSuccess();

        if (isSuccess) {
            return ResponseEntity.ok(response);
        } else {
            // Trong một hệ thống thực tế, bạn có thể map các mã lỗi khác nhau
            // (ví dụ: "01" -> 404 Not Found, "02" -> 409 Conflict)
            // nhưng Bad Request (400) là một lựa chọn mặc định an toàn cho các lỗi nghiệp vụ.
            return ResponseEntity.badRequest().body(response);
        }
    }
}