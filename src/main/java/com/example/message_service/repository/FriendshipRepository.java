// Trong FriendshipRepository.java
package com.example.message_service.repository;

import com.example.message_service.model.Friendship;
import com.example.message_service.model.FriendshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {

    // GỢI Ý 4: Một câu query để kiểm tra tồn tại 2 chiều
    @Query("SELECT COUNT(f) > 0 FROM Friendship f " +
            "WHERE (f.sender.id = :userId1 AND f.receiver.id = :userId2) OR " +
            "(f.sender.id = :userId2 AND f.receiver.id = :userId1)")
    boolean friendshipExists(@Param("userId1") String userId1, @Param("userId2") String userId2);

    // GỢI Ý 3: Tối ưu N+1 query khi lấy danh sách bạn bè
    @Query("SELECT f FROM Friendship f " +
            "JOIN FETCH f.sender " +
            "JOIN FETCH f.receiver " +
            "WHERE (f.sender.id = :userId OR f.receiver.id = :userId) AND f.status = 'accepted'")
    List<Friendship> findAcceptedFriendsWithUsers(@Param("userId") String userId);

    // GỢI Ý: Tối ưu N+1 query khi lấy danh sách lời mời
    @Query("SELECT f FROM Friendship f " +
            "JOIN FETCH f.sender " +
            "WHERE f.receiver.id = :userId AND f.status = 'pending'")
    List<Friendship> findPendingRequestsForUser(@Param("userId") String userId);
}