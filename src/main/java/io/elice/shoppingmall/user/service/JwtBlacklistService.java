package io.elice.shoppingmall.user.service;

import java.time.Duration;
import java.util.Date;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class JwtBlacklistService {

    private static final String BLACKLIST_KEY_PREFIX = "jwt_blacklist_";
    private final RedisTemplate<String, String> redisTemplate;

    public JwtBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklist(String token, Date expirationDate) {
        String key = BLACKLIST_KEY_PREFIX + token;
        Duration expiration = Duration.between(new Date().toInstant(), expirationDate.toInstant());
        redisTemplate.opsForValue().set(key, token, expiration);
    }

    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_KEY_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
