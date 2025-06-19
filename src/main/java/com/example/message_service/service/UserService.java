package com.example.message_service.service;

import com.example.message_service.components.JwtTokenUtil;
import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.RegisterRequest;
import com.example.message_service.infrastructure.RedisToken;
import com.example.message_service.model.User;
import com.example.message_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisToken redisToken;

    /**
     * Đăng nhập người dùng và sinh token
     */
    public ApiResponse<String> loginUser(String username, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ApiResponse.error("01", "User không tồn tại");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ApiResponse.error("02", "Mật khẩu không đúng");
        }

        String token = jwtTokenUtil.generateToken(user);
        long expirationTime = jwtTokenUtil.getExpirationTime(token);

        redisToken.saveToken(user.getUsername(), token, expirationTime);

        return ApiResponse.success("00", "Đăng nhập thành công", token);
    }

    /**
     * Đăng ký người dùng mới
     */
    public ApiResponse<String> registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ApiResponse.error("01", "Username đã tồn tại");
        }

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            return ApiResponse.error("02", "Số điện thoại đã được sử dụng");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ApiResponse.error("03", "Email đã được sử dụng");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setDisplayName(request.getDisplayName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        return ApiResponse.success("00", "Đăng ký thành công", null);
    }

    /**
     * Lấy thông tin người dùng theo ID
     */
    public ApiResponse<User> getByUserId(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ApiResponse.error("01", "User không tồn tại");
        }

        return ApiResponse.success("00", userOptional.get());
    }

    public ApiResponse<String> logoutUser(String username, String token) {
        // Kiểm tra token hợp lệ trước khi xóa
        if (!redisToken.isTokenValid(username, token)) {
            return ApiResponse.error("01", "Token không hợp lệ hoặc đã hết hạn");
        }

        // Xóa token khỏi Redis (logout)
        redisToken.deleteToken(username);
        return ApiResponse.success("00", "Logout thành công", null);
    }


}
