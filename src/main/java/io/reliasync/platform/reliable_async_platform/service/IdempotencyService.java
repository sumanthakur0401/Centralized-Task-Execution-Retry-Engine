package io.reliasync.platform.reliable_async_platform.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Handles idempotency using Redis.
 *
 * Prevents duplicate execution of the same task
 * across retries and duplicate requests.
 */
@Service
public class IdempotencyService {

    private static final String IDEMPOTENCY_PREFIX = "idem:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Try to acquire idempotency lock.
     * Returns true if this is the first execution.
     */
    public boolean acquire(String idempotencyKey) {
        String key = IDEMPOTENCY_PREFIX + idempotencyKey;

        Boolean success = redisTemplate
                .opsForValue()
                .setIfAbsent(key, "IN_PROGRESS", TTL);

        return Boolean.TRUE.equals(success);
    }

    /**
     * Mark execution as completed.
     */
    public void markCompleted(String idempotencyKey) {
        String key = IDEMPOTENCY_PREFIX + idempotencyKey;
        redisTemplate.opsForValue().set(key, "COMPLETED", TTL);
    }

    /**
     * Check if execution is already completed.
     */
    public boolean isCompleted(String idempotencyKey) {
        String key = IDEMPOTENCY_PREFIX + idempotencyKey;
        return redisTemplate.hasKey(key);
    }
}
