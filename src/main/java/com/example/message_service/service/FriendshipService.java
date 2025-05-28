package com.example.message_service.service;

import com.example.message_service.model.Friendship;
import com.example.message_service.model.User;
import com.example.message_service.repository.FriendshipRepository;
import com.example.message_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    // Gửi lời mời kết bạn
    public boolean sendFriendRequest(String senderId, String receiverId) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            User sender = senderOpt.get();
            User receiver = receiverOpt.get();

            // Kiểm tra xem đã có mối quan hệ kết bạn chưa
            Optional<Friendship> existingFriendship = friendshipRepository.findBySenderAndReceiver(sender, receiver);
            if (existingFriendship.isPresent()) {
                return false;  // Mối quan hệ kết bạn đã tồn tại
            }

            // Tạo và lưu mối quan hệ kết bạn
            Friendship friendship = new Friendship();
            friendship.setSender(sender);
            friendship.setReceiver(receiver);
            friendship.setStatus("pending");
            friendshipRepository.save(friendship);
            return true;
        }
        return false;
    }

    // Chấp nhận lời mời kết bạn
    public boolean acceptFriendRequest(String senderId, String receiverId) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            User sender = senderOpt.get();
            User receiver = receiverOpt.get();

            // Kiểm tra mối quan hệ kết bạn
            Optional<Friendship> friendshipOpt = friendshipRepository.findBySenderAndReceiver(sender, receiver);
            if (friendshipOpt.isPresent() && friendshipOpt.get().getStatus().equals("pending")) {
                Friendship friendship = friendshipOpt.get();
                friendship.setStatus("accepted");
                friendship.setAcceptedAt(java.time.LocalDateTime.now());
                friendshipRepository.save(friendship);
                return true;
            }
        }
        return false;
    }

    // Từ chối lời mời kết bạn
    public boolean rejectFriendRequest(String senderId, String receiverId) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            User sender = senderOpt.get();
            User receiver = receiverOpt.get();

            // Kiểm tra mối quan hệ kết bạn
            Optional<Friendship> friendshipOpt = friendshipRepository.findBySenderAndReceiver(sender, receiver);
            if (friendshipOpt.isPresent()) {
                friendshipRepository.delete(friendshipOpt.get());
                return true;
            }
        }
        return false;
    }

    // Lấy tất cả các mối quan hệ kết bạn của một người (bất kể họ là người gửi hay người nhận)
    public List<Friendship> getFriendships(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return friendshipRepository.findBySenderOrReceiver(user, user);
        }
        return null;  // Hoặc ném ra ngoại lệ nếu không tìm thấy người dùng
    }

    // Lấy tất cả lời mời kết bạn đang chờ chấp nhận (status = "pending") cho một người nhận
    public List<Friendship> getPendingRequests(String receiverId) {
        Optional<User> receiverOpt = userRepository.findById(receiverId);
        if (receiverOpt.isPresent()) {
            User receiver = receiverOpt.get();
            return friendshipRepository.findByStatusAndReceiver("pending", receiver);
        }
        return null;  // Hoặc ném ra ngoại lệ nếu không tìm thấy người nhận
    }
}
