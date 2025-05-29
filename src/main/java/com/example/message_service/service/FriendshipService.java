package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.model.Friendship;
import com.example.message_service.model.User;
import com.example.message_service.repository.FriendshipRepository;
import com.example.message_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    public ApiResponse<String> sendFriendRequest(String senderId, String receiverId) {
        // Tìm sender và receiver
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            return ApiResponse.error("01", "Người dùng không tồn tại");
        }

        User sender = senderOpt.get();
        User receiver = receiverOpt.get();

        // Kiểm tra xem đã có mối quan hệ kết bạn chưa (cả 2 chiều)
        boolean exists = friendshipRepository.existsBySenderAndReceiver(sender, receiver)
                || friendshipRepository.existsBySenderAndReceiver(receiver, sender);
        if (exists) {
            return ApiResponse.error("02", "Mối quan hệ kết bạn đã tồn tại");
        }

        // Tạo và lưu mối quan hệ kết bạn mới
        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setStatus("pending");
        friendshipRepository.save(friendship);

        return ApiResponse.success("00","Lời mời kết bạn đã được gửi");
    }


    public ApiResponse<String> acceptFriendRequest(String senderId, String receiverId) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            return ApiResponse.error("02", "Người dùng không tồn tại");
        }

        User sender = senderOpt.get();
        User receiver = receiverOpt.get();

        Optional<Friendship> friendshipOpt = friendshipRepository.findBySenderAndReceiver(sender, receiver);
        if (friendshipOpt.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy lời mời kết bạn");
        }

        Friendship friendship = friendshipOpt.get();
        if (!"pending".equals(friendship.getStatus())) {
            return ApiResponse.error("04", "Lời mời kết bạn không ở trạng thái chờ");
        }

        friendship.setStatus("accepted");
        friendship.setAcceptedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);

        return ApiResponse.success("00", "Lời mời kết bạn đã được chấp nhận", null);
    }


    // Từ chối lời mời kết bạn
    public ApiResponse<String> rejectFriendRequest(String senderId, String receiverId) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            return ApiResponse.error("02", "Người dùng không tồn tại");
        }

        User sender = senderOpt.get();
        User receiver = receiverOpt.get();

        Optional<Friendship> friendshipOpt = friendshipRepository.findBySenderAndReceiver(sender, receiver);
        if (friendshipOpt.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy lời mời kết bạn để từ chối");
        }

        friendshipRepository.delete(friendshipOpt.get());
        return ApiResponse.success("00", "Lời mời kết bạn đã bị từ chối", null);
    }


    // Lấy tất cả các mối quan hệ kết bạn của một người (bất kể họ là người gửi hay người nhận)
    public ApiResponse<List<Friendship>> getFriendships(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("02", "Người dùng không tồn tại");
        }

        User user = userOpt.get();
        List<Friendship> friendships = friendshipRepository.findBySenderOrReceiver(user, user);
        return ApiResponse.success("00", "Lấy danh sách mối quan hệ thành công", friendships);
    }


    // Lấy tất cả lời mời kết bạn đang chờ chấp nhận (status = "pending") cho một người nhận
    public ApiResponse<List<Friendship>> getPendingRequests(String receiverId) {
        Optional<User> receiverOpt = userRepository.findById(receiverId);
        if (receiverOpt.isEmpty()) {
            return ApiResponse.error("02", "Người nhận không tồn tại");
        }

        User receiver = receiverOpt.get();
        List<Friendship> requests = friendshipRepository.findByStatusAndReceiver("pending", receiver);
        return ApiResponse.success("00", "Lấy danh sách lời mời kết bạn đang chờ thành công", requests);
    }

}
