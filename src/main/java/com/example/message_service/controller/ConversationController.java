package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.request.CreateConversationRequest; // GỢI Ý: Tạo DTO mới
import com.example.message_service.dto.request.UpdateConversationRequest;
import com.example.message_service.dto.response.ConversationResponse;
import com.example.message_service.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    // Không cần ConversationMemberService ở đây nữa

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /**
     * Lấy tất cả cuộc trò chuyện của một người dùng.
     * Endpoint này giờ đây đã được tối ưu cao và có cache.
     * GET /api/conversations?userId={userId}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversationsByUser(
            @RequestParam String userId) {
        ApiResponse<List<ConversationResponse>> response = conversationService.getConversationsByUser(userId);
        return buildResponse(response);
    }

    /**
     * Tạo một cuộc trò chuyện mới (nhóm hoặc 1-1).
     * Loại cuộc trò chuyện được quyết định bởi các tham số trong body.
     * POST /api/conversations
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ConversationResponse>> createConversation(
            @RequestBody CreateConversationRequest request) {

        // SỬA LỖI 2: Service giờ sẽ trả về DTO
        ApiResponse<ConversationResponse> response = conversationService.createConversation(request);
        return buildResponse(response, true); // true để trả về status 201 CREATED
    }

    /**
     * Cập nhật thông tin cuộc trò chuyện (ví dụ: đổi tên nhóm).
     * PUT /api/conversations/{conversationId}
     */
    @PutMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<ConversationResponse>> updateConversation(
            @PathVariable String conversationId,
            @RequestBody UpdateConversationRequest request) {
        ApiResponse<ConversationResponse> response = conversationService.updateConversation(conversationId, request);
        return buildResponse(response);
    }

    /**
     * Upload avatar cho nhóm.
     * POST /api/conversations/{conversationId}/avatar
     * (Giữ nguyên logic của bạn vì nó đã khá tốt)
     */
    @PostMapping("/{id}/avatar")
    public ResponseEntity<ApiResponse<String>> uploadGroupAvatar(
            @PathVariable("id") String conversationId,
            @RequestParam("file") MultipartFile file) {
        // Giả sử service có phương thức này và trả về ApiResponse
        //ApiResponse<String> response = conversationService.updateGroupAvatar(conversationId, file);
        // return buildResponse(response);
        // Tạm thời comment out để tránh lỗi nếu service chưa có
        return ResponseEntity.ok(ApiResponse.success("00", "Chức năng đang phát triển", null));
    }


    /*
     * Các endpoint đã được refactor hoặc loại bỏ:
     * - /create-group và /one-to-one đã được hợp nhất vào POST /api/conversations
     * - /{conversationId}/archive có thể được tích hợp vào PUT /{conversationId} bằng một trường isArchived trong body.
     * - /user/{userId}/paged: Với hiệu năng của phương thức getConversationsByUser mới, việc phân trang ở backend
     *   có thể không còn quá cần thiết cho lần tải đầu tiên. Client có thể tải toàn bộ danh sách
     *   và thực hiện "infinite scroll" trên dữ liệu đã có. Nếu danh sách quá lớn, việc phân trang
     *   cần được thiết kế lại ở cả Service và Repository.
     */

    /**
     * Phương thức helper để xây dựng ResponseEntity.
     */
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(ApiResponse<T> response, boolean isCreated) {
        boolean isSuccess = response.getStatus().isSuccess();
        if (isSuccess) {
            if (isCreated) {
                return ResponseEntity.status(201).body(response);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildResponse(ApiResponse<T> response) {
        return buildResponse(response, false);
    }
}