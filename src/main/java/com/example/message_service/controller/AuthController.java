package com.example.message_service.controller;


import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.LoginRequest;
import com.example.message_service.dto.request.RegisterRequest;
import com.example.message_service.model.User;
import com.example.message_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid  @RequestBody LoginRequest loginRequest) {
        try {
            ApiResponse<String> response = userService.loginUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("99", "Lỗi hệ thống"));
        }
    }

    // Đăng ký người dùng mới
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid  @RequestBody RegisterRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong during registration.");
        }
    }

}
