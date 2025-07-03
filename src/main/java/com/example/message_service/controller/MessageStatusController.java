package com.example.message_service.controller;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.model.MessageStatus;
import com.example.message_service.service.MessageStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Import
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
// GỢI Ý 2: Thiết kế lại URL để lấy message làm tài nguyên gốc
@RequestMapping("/api/messages/{messageId}/statuses")
public class MessageStatusController {

    private final MessageStatusService messageStatusService;

    @Autowired
    public MessageStatusController(MessageStatusService messageStatusService) {
        this.messageStatusService = messageStatusService;
    }

    /**
     * Lấy tất cả trạng thái của một tin nhắn (ví dụ: danh sách những người đã xem).
     * GET /api/messages/{messageId}/statuses
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageStatus>>> getStatusesByMessage(@PathVariable String messageId) {
        ApiResponse<List<MessageStatus>> response = messageStatusService.getStatusesByMessage(messageId);
        return buildResponse(response);
    }

    /**
     * Cập nhật hoặc tạo mới trạng thái của một tin nhắn cho một người dùng cụ thể.
     * Đây là endpoint chính để client báo "đã xem", "đã nhận".
     * PUT /api/messages/{messageId}/statuses
     * Body: { "userId": "user-abc", "status": "read" }
     */
    @PutMapping
    public ResponseEntity<ApiResponse<MessageStatus>> upsertMessageStatus(
            @PathVariable String messageId,
            @RequestBody Map<String, String> payload) {

        String userId = payload.get("userId");
        String newStatus = payload.get("status");

        if (userId == null || newStatus == null) {
            return buildResponse(ApiResponse.error("10", "userId và status là bắt buộc"));
        }

        ApiResponse<MessageStatus> response = messageStatusService.upsertMessageStatus(messageId, userId, newStatus);
        return buildResponse(response);
    }

    /**
     * Phương thức helper để xây dựng ResponseEntity từ ApiResponse.
     */
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(ApiResponse<T> response) {
        boolean isSuccess = response.getStatus().isSuccess();
        if (isSuccess) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /*
    Lưu ý về các endpoint cũ:
    - GET /message/{messageId}/user/{userId} và GET /user/{userId}/status/{status}:
      Các endpoint này thường ít được sử dụng ở phía client. Nếu thực sự cần, bạn có thể tạo lại
      chúng trong service và controller. Tuy nhiên, việc lấy tất cả trạng thái của một message
      (GET /api/messages/{messageId}/statuses) rồi lọc ở client thường đủ dùng và hiệu quả hơn
      nhờ caching ở backend.

    - POST / và PUT /{messageStatusId}:
      Đã được thay thế hoàn toàn bằng endpoint PUT / mới, mạnh mẽ và logic hơn.
    */
}