package com.media.flow.repository;

import com.media.flow.constant.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 11.08.2026
 */
@Repository
@RequiredArgsConstructor
public class RedisSetRepositoryImpl implements RedisSetRepository {
    private final static int COUNT = 500;
    private static final Duration FAMILY_TTL = Duration.ofMinutes(30);
    private final StringRedisTemplate stringRedisTemplate;

    public Instant setMediaFileFamilyExpirations(final UUID originFileId) {
        final Instant expiresAt = Instant.now().plus(FAMILY_TTL);
        stringRedisTemplate.opsForZSet()
            .add(RedisKeys.EXPIRATIONS,
                originFileId.toString(),
                expiresAt.getEpochSecond());
        return expiresAt;
    }

    public Set<String> findExpiredOriginFileId(final Instant now) {
        return stringRedisTemplate.opsForZSet()
            .rangeByScore(
                RedisKeys.EXPIRATIONS,
                0,
                now.getEpochSecond(),
                0,
                COUNT
            );
    }

    @Override
    public void delete(final Set<String> expiredIds) {
        if (expiredIds.isEmpty()) {
            return;
        }
        stringRedisTemplate.opsForZSet().remove(RedisKeys.EXPIRATIONS, expiredIds.toArray());
    }
}
