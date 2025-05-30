package com.example.message_service.service;


import com.example.message_service.components.JwtTokenUtil;
import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.RegisterRequest;
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



    public ApiResponse<String> loginUser(String username, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ApiResponse.error("01", "User not found");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ApiResponse.error("02", "Mật khẩu không đúng");
        }

        String token = jwtTokenUtil.generateToken(user);

        return ApiResponse.success("00", "Đăng nhập thành công", token);
    }


    // Đăng ký người dùng mới
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

    public ApiResponse<User> getByUserId(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("01", "User not found");
        }
        return ApiResponse.success("00", userOptional.get());
    }

}
