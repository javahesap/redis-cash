package com.haydikodlayalim.redisapp.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserRateLimitService {

    private static final String REDIS_KEY_PREFIX = "user:request:limit:";
    private static final int MAX_REQUESTS = 5;
    private static final int BLOCK_DURATION_SECONDS = 2; // 5 dakika

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void checkAndIncrementRequestCount(String userId) {
        String key = REDIS_KEY_PREFIX + userId;

        Long count = redisTemplate.opsForValue().increment(key, 1);
        if (count != null && count == 1) {
            // İlk istek, TTL (Time to Live) ayarla
            redisTemplate.expire(key, BLOCK_DURATION_SECONDS, TimeUnit.SECONDS);
        }

        if (count != null && count > MAX_REQUESTS) {
            // Bloklandı, işlemleri burada gerçekleştir
            throw new RuntimeException("Beşten fazla istek yaptınız. Bloklanacaksınız!");
        }
    }

    public boolean isUserBlocked(String userId) {
        String key = REDIS_KEY_PREFIX + userId;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null && ttl > 0;
    }
}
