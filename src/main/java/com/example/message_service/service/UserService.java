package com.example.message_service.service;

import com.example.message_service.components.JwtTokenUtil;
import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.RegisterRequest;
import com.example.message_service.infrastructure.RedisToken;
import com.example.message_service.model.User;
import com.example.message_service.repository.UserRepository;
import com.example.message_service.service.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor // Sử dụng constructor injection, thay thế cho các @Autowired riêng lẻ
public class UserService {

    // Các dependency được inject qua constructor nhờ @RequiredArgsConstructor
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisToken redisToken;
    private final FileStorageService fileStorageService; // Dependency mới để xử lý file

    /**
     * Đăng nhập người dùng và sinh token.
     */
    @Transactional(readOnly = true)
    public ApiResponse<String> loginUser(String email, String password) throws Exception {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return ApiResponse.<String>error("02", "Mật khẩu không đúng");
                    }
                    try {
                        String token = jwtTokenUtil.generateToken(user);
                        long expirationTime = jwtTokenUtil.getExpirationTime(token);
                        redisToken.saveToken(user.getEmail(), token, expirationTime);
                        return ApiResponse.success("00", "Đăng nhập thành công", token);
                    } catch (Exception e) {
                        // Nên có một logger ở đây
                        return ApiResponse.<String>error("99", "Lỗi sinh token");
                    }
                })
                .orElse(ApiResponse.error("01", "User không tồn tại"));
    }

    /**
     * Đăng ký người dùng mới.
     */
    @Transactional
    public ApiResponse<String> registerUser(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return ApiResponse.error("02", "Số điện thoại đã được sử dụng");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("03", "Email đã được sử dụng");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        // @PrePersist trong User.java sẽ tự động tạo ID
        user.setPassword(encodedPassword);
        user.setDisplayName(request.getDisplayName());
        user.setAvatarUrl(request.getAvatarUrl()); // Có thể set avatar mặc định
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        return ApiResponse.success("00", "Đăng ký thành công", null);
    }

    /**
     * Lấy thông tin người dùng theo ID (có cache).
     */
    @Cacheable(value = "users", key = "#userId")
    @Transactional(readOnly = true)
    public ApiResponse<User> getByUserId(String userId) {
        return userRepository.findById(userId)
                .map(user -> ApiResponse.success("00", "Lấy thông tin user thành công", user))
                .orElse(ApiResponse.error("01", "User không tồn tại"));
    }

    /**
     * Cập nhật avatar cho người dùng.
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId") // Xóa cache của user này khi avatar thay đổi
    public ApiResponse<String> updateUserAvatar(String userId, MultipartFile file) {
        try {
            // Nhờ FileStorageService lưu file và lấy về URL
            String avatarUrl = fileStorageService.storeUserAvatar(file);

            // Tìm user và cập nhật
            return userRepository.findById(userId)
                    .map(user -> {
                        // TODO: Có thể thêm logic xóa file avatar cũ ở đây nếu cần
                        user.setAvatarUrl(avatarUrl);
                        userRepository.save(user);
                        return ApiResponse.success("00", "Cập nhật avatar thành công", avatarUrl);
                    })
                    .orElse(ApiResponse.error("01", "User không tồn tại"));
        } catch (Exception e) {
            // Log lỗi
            return ApiResponse.error("99", "Lỗi khi cập nhật avatar: " + e.getMessage());
        }
    }

    /**
     * Đăng xuất người dùng.
     */
    public ApiResponse<String> logoutUser(String username, String token) {
        if (!redisToken.isTokenValid(username, token)) {
            return ApiResponse.error("01", "Token không hợp lệ hoặc đã hết hạn");
        }
        redisToken.deleteToken(username);
        return ApiResponse.success("00", "Logout thành công", null);
    }
}