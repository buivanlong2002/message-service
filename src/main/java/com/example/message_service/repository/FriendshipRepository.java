package com.example.message_service.repository;

import com.example.message_service.model.Friendship;
import com.example.message_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, String> {

    Optional<Friendship> findBySenderAndReceiver(User sender, User receiver);

    List<Friendship> findBySenderOrReceiver(User user, User user2);

    List<Friendship> findByStatusAndReceiver(String status, User receiver);

    List<Friendship> findByStatusAndSender(String status, User sender);

    boolean existsBySenderAndReceiver(User sender, User receiver);

    // ✅ Kiểm tra quan hệ chặn cụ thể
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, String status);

    // ✅ Danh sách người mà user đã chặn
    List<Friendship> findBySenderAndStatus(User sender, String status);

    // ✅ Danh sách người đã chặn user
    List<Friendship> findByReceiverAndStatus(User receiver, String status);
}
