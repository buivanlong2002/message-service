package com.example.message_service.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisToken {

    private final StringRedisTemplate redisTemplate;

    // Lưu token với username làm key
    public void saveToken(String username, String token, long expirationMillis) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        long currentMillis = System.currentTimeMillis();
        long ttl = expirationMillis - currentMillis;

        System.out.println(">>> Saving token for user: " + username);
        System.out.println(">>> Current millis: " + currentMillis);
        System.out.println(">>> Expiration millis: " + expirationMillis);
        System.out.println(">>> TTL (ms): " + ttl);

        if (ttl > 0) {
            String key = "auth:token:" + username;
            ops.set(key, token, ttl, TimeUnit.MILLISECONDS);
            System.out.println(">>> Token saved to Redis with key: " + key);
        } else {
            System.err.println(">>> Cannot save token: TTL <= 0");
        }
    }


    public String getToken(String username) {
        return redisTemplate.opsForValue().get("token:" + username);
    }

    public void deleteToken(String username) {
        redisTemplate.delete("token:" + username);
    }

    // So sánh token hiện tại với token lưu trên Redis
    public boolean isTokenValid(String username, String token) {
        String saved = getToken(username);
        return saved != null && saved.equals(token);
    }
}
