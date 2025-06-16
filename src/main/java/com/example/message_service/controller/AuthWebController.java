package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.LoginRequest;
import com.example.message_service.dto.request.RegisterRequest;
import com.example.message_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;

@Controller
public class AuthWebController {

    @Autowired
    private RestTemplate restTemplate;  // Để gửi HTTP request đến API backend

    // Trang đăng nhập
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";  // Trả về trang login.html
    }

    // Trang đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";  // Trả về trang register.html
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String loginUser(LoginRequest loginRequest) {
        try {
            // Gửi request đến API backend (AuthController) sử dụng RestTemplate
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(
                    "http://localhost:8080/api/auth/login",  // URL của API
                    HttpMethod.POST,  // Phương thức HTTP
                    new HttpEntity<>(loginRequest),  // Đóng gói loginRequest vào HttpEntity
                    new ParameterizedTypeReference<ApiResponse<String>>() {}  // Chỉ rõ kiểu trả về
            );

            // Kiểm tra kết quả từ API
            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/home";  // Nếu đăng nhập thành công, chuyển đến trang home
            } else {
                // Nếu đăng nhập không thành công, quay lại trang login với thông báo lỗi
                return "redirect:/login?error=true";
            }
        } catch (Exception ex) {
            // Xử lý lỗi (lỗi kết nối API chẳng hạn)
            return "redirect:/login?error=true";
        }
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerUser(RegisterRequest registerRequest) {
        try {
            // Gửi request đến API backend (AuthController) sử dụng RestTemplate
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(
                    "http://localhost:8080/api/auth/register",  // URL của API
                    HttpMethod.POST,  // Phương thức HTTP
                    new HttpEntity<>(registerRequest),  // Đóng gói registerRequest vào HttpEntity
                    new ParameterizedTypeReference<ApiResponse<String>>() {}  // Chỉ rõ kiểu trả về
            );

            // Kiểm tra kết quả từ API
            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/login";  // Sau khi đăng ký thành công, chuyển đến trang đăng nhập
            } else {
                // Nếu đăng ký không thành công, quay lại trang đăng ký với thông báo lỗi
                return "redirect:/register?error=true";
            }
        } catch (Exception ex) {
            // Xử lý lỗi (lỗi kết nối API chẳng hạn)
            return "redirect:/register?error=true";
        }
    }
}
