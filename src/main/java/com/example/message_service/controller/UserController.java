package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse; // Import
import com.example.message_service.model.User;
import com.example.message_service.service.UserService; // Import UserService
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    // Bỏ UserRepository và @Value, thay bằng UserService
    private final UserService userService;

    /**
     * Upload avatar cho người dùng đang đăng nhập.
     * Controller chỉ nhận request và ủy quyền cho Service.
     */
    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {

        // Gọi service để xử lý toàn bộ logic
        ApiResponse<String> response = userService.updateUserAvatar(user.getId(), file);

        // Trả về response một cách nhất quán
        return buildResponse(response);
    }

    /**
     * Phương thức helper để xây dựng ResponseEntity.
     */
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(ApiResponse<T> response) {
        if (response.getStatus().isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}