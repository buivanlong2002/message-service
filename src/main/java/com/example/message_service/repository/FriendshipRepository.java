package com.example.message_service.repository;

import com.example.message_service.model.Friendship;
import com.example.message_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, String> {

    Optional<Friendship> findBySenderAndReceiver(User sender, User receiver);

    List<Friendship> findBySenderOrReceiver(User user, User user2);

    List<Friendship> findByStatusAndReceiver(String status, User receiver);
}
