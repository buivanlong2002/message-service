package com.example.message_service.service;

import com.example.message_service.dto.ApiResponse;
import com.example.message_service.dto.response.FriendRequestResponse; // Sửa DTO để phù hợp
import com.example.message_service.model.Friendship;
import com.example.message_service.model.FriendshipId;
import com.example.message_service.model.User;
import com.example.message_service.repository.FriendshipRepository;
import com.example.message_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    // Khi có thay đổi về bạn bè, xóa cache của cả 2 người dùng
    @Caching(evict = {
            @CacheEvict(value = "friends", key = "#senderId"),
            @CacheEvict(value = "friends", key = "#receiverId"),
            @CacheEvict(value = "pendingRequests", key = "#receiverId")
    })
    @Transactional
    public ApiResponse<String> sendFriendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId)) {
            return ApiResponse.error("03", "Bạn không thể tự kết bạn với chính mình");
        }

        // Kiểm tra tồn tại 2 chiều bằng query đã tối ưu
        if (friendshipRepository.friendshipExists(senderId, receiverId)) {
            return ApiResponse.error("02", "Mối quan hệ kết bạn đã tồn tại hoặc đang chờ xử lý.");
        }

        User sender = userRepository.findById(senderId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (sender == null || receiver == null) {
            return ApiResponse.error("01", "Người dùng không tồn tại");
        }

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setStatus("pending");
        // @CreationTimestamp sẽ tự động điền requestedAt

        friendshipRepository.save(friendship);
        return ApiResponse.success("00", "Lời mời kết bạn đã được gửi");
    }

    @Caching(evict = {
            @CacheEvict(value = "friends", key = "#senderId"),
            @CacheEvict(value = "friends", key = "#receiverId"),
            @CacheEvict(value = "pendingRequests", key = "#receiverId")
    })
    @Transactional
    public ApiResponse<String> acceptFriendRequest(String senderId, String receiverId) {
        FriendshipId friendshipId = new FriendshipId(senderId, receiverId);
        Optional<Friendship> friendshipOpt = friendshipRepository.findById(friendshipId);

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

    @Caching(evict = {
            @CacheEvict(value = "friends", key = "#senderId"),
            @CacheEvict(value = "friends", key = "#receiverId"),
            @CacheEvict(value = "pendingRequests", key = "#receiverId")
    })
    @Transactional
    public ApiResponse<String> rejectFriendRequest(String senderId, String receiverId) {
        FriendshipId friendshipId = new FriendshipId(senderId, receiverId);
        Optional<Friendship> friendshipOpt = friendshipRepository.findById(friendshipId);

        if (friendshipOpt.isEmpty()) {
            return ApiResponse.error("03", "Không tìm thấy lời mời kết bạn để từ chối");
        }

        // Nếu tìm thấy, thực hiện xóa
        friendshipRepository.delete(friendshipOpt.get());
        return ApiResponse.success("00", "Lời mời kết bạn đã bị từ chối", null);
    }

    @Cacheable(value = "friends", key = "#userId")
    @Transactional(readOnly = true)
    public ApiResponse<List<User>> getFriendships(String userId) {
        // Dùng query đã tối ưu N+1
        List<Friendship> friendships = friendshipRepository.findAcceptedFriendsWithUsers(userId);

        List<User> friends = friendships.stream()
                .map(f -> f.getSender().getId().equals(userId) ? f.getReceiver() : f.getSender())
                .collect(Collectors.toList());

        return ApiResponse.success("00", "Lấy danh sách bạn bè thành công", friends);
    }

    @Cacheable(value = "pendingRequests", key = "#userId")
    @Transactional(readOnly = true)
    public ApiResponse<List<FriendRequestResponse>> getPendingRequests(String userId) {
        // Dùng query đã tối ưu N+1
        List<Friendship> pendingRequests = friendshipRepository.findPendingRequestsForUser(userId);

        List<FriendRequestResponse> dtoList = pendingRequests.stream()
                .map(f -> new FriendRequestResponse(
                        f.getSender().getId(),
                        f.getSender().getDisplayName(),
                        f.getSender().getAvatarUrl(),
                        f.getRequestedAt()
                ))
                .collect(Collectors.toList());

        return ApiResponse.success("00", "Lấy danh sách lời mời kết bạn đang chờ thành công", dtoList);
    }
}