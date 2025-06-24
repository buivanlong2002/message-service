package com.example.message_service.controller;

import com.example.message_service.model.User;
import com.example.message_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @Value("${user.avatar.upload-dir}")
    private String uploadDir;

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal User user) {
        try {
            // Đảm bảo thư mục tồn tại
            Path directory = Paths.get(uploadDir);
            Files.createDirectories(directory);

            // Đặt tên file duy nhất
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = directory.resolve(fileName);

            // Ghi file lên ổ đĩa
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Lưu URL tương đối vào DB (ví dụ: /uploads/...)
            String avatarUrl = "/uploads/avatar/" + fileName;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return ResponseEntity.ok().body(avatarUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Lỗi khi upload avatar");
        }
    }
}
