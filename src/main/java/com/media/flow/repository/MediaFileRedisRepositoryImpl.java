package com.media.flow.repository;

import com.media.flow.constant.RedisKeys;
import com.media.flow.model.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 02.08.2026
 */
@Repository
@RequiredArgsConstructor
public class MediaFileRedisRepositoryImpl implements MediaFileRedisRepository {
    private static final Duration FAMILY_TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, MediaFile> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public LocalDateTime save(final MediaFile mediaFile) {
        final UUID mediaId = mediaFile.getId();
        final UUID originFileId = mediaFile.getOriginFileId();

        saveMediaFileData(mediaId, mediaFile);
        saveMediaFileSetFamily(originFileId, mediaId);
        return setMediaFileFamilyExpirations(originFileId);
    }

    private void saveMediaFileData(final UUID mediaId, final MediaFile mediaFile) {
        redisTemplate.opsForValue()
            .set(RedisKeys.media(mediaId), mediaFile);
    }

    private void saveMediaFileSetFamily(final UUID originFileId, final UUID mediaId) {
        stringRedisTemplate.opsForSet()
            .add(
                RedisKeys.mediaFamily(originFileId),
                mediaId.toString()
            );
    }

    private LocalDateTime setMediaFileFamilyExpirations(final UUID originFileId) {
        final LocalDateTime expiresAt = LocalDateTime.now().plus(FAMILY_TTL);
        stringRedisTemplate.opsForZSet()
            .add(RedisKeys.EXPIRATIONS,
                originFileId.toString(),
                expiresAt.toEpochSecond(ZoneOffset.UTC));
        return expiresAt;
    }
}
