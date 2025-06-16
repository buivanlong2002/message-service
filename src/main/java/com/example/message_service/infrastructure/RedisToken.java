package com.example.message_service.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisToken {

    private final StringRedisTemplate redisTemplate;

    private String getKey(String username) {
        return "auth:token:" + username;
    }

    public void saveToken(String username, String token, long expirationMillis) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        long ttl = expirationMillis - System.currentTimeMillis();

        if (ttl > 0) {
            String key = getKey(username);
            ops.set(key, token, ttl, TimeUnit.MILLISECONDS);
        } else {
            System.err.println(" Cannot save token: TTL <= 0");
        }
    }

    // Lấy token từ Redis
    public String getToken(String username) {
        String key = getKey(username);
        String token = redisTemplate.opsForValue().get(key);
        return token;
    }

    // Xoá token
    public void deleteToken(String username) {
        String key = getKey(username);
        redisTemplate.delete(key);
    }

    // Kiểm tra token có hợp lệ không
    public boolean isTokenValid(String username, String token) {
        String savedToken = getToken(username);
        boolean valid = savedToken != null && savedToken.equals(token);
        return valid;
    }
}
