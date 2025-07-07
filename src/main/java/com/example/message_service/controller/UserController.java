package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.model.User;
import com.example.message_service.repository.UserRepository;
import com.example.message_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @Value("${user.avatar.upload-dir}")
    private String uploadDir;

    @Autowired
    private UserService userService;

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal User user) {
        return userService.uploadAvatar(file, user);
    }

    @GetMapping("/profile")
    public ApiResponse<?> getMyProfile() {
        String loginIdentifier = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> userOptional = userRepository.findByEmail(loginIdentifier); // Hoặc findByPhoneNumber

        if (userOptional.isEmpty()) {
            return ApiResponse.error("404", "Không tìm thấy người dùng với định danh từ token: " + loginIdentifier);
        }

        return ApiResponse.success("00", "Lấy thông tin profile thành công", userOptional.get());
    }

    @GetMapping("/{userId}")
    public ApiResponse<User> getUserById(@PathVariable String userId) {
        return userService.getByUserId(userId);
    }

    @GetMapping("/search")
    public List<User> searchUsersByEmail(@RequestParam String email) {
        return userService.searchByEmail(email);
    }
}
