package com.media.flow.service.impl;

import com.media.flow.dto.MediaFileDto;
import com.media.flow.mapper.MediaFileMapper;
import com.media.flow.model.MediaFile;
import com.media.flow.record.StoredMedia;
import com.media.flow.repository.MediaFileRedisRepository;
import com.media.flow.service.FfmpegService;
import com.media.flow.service.MediaFileOperationsService;
import com.media.flow.service.StorageService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 29.07.2026
 */
@Service
@RequiredArgsConstructor
public class MediaFileOperationsServiceImpl implements MediaFileOperationsService {
    private static final Logger log = LoggerFactory.getLogger(MediaFileOperationsServiceImpl.class);
    private final StorageService storageService;
    private final FfmpegService ffmpegService;
    private final MediaFileRedisRepository redisRepository;
    private final MediaFileMapper mediaFileMapper;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Override
    public MediaFileDto uploadAndGetMediaFileData(final MultipartFile file) {
        final StoredMedia temporary = storageService.saveTemporaryFile(file);
        try {
            final MediaFileDto mediaFileDto = ffmpegService.getVideoParams(temporary.localPath());
            final MediaFile mediaFile = mediaFileMapper.toEntity(mediaFileDto);
            final UUID uuid = temporary.id();
            final String permanentPath = storageService.savePersistFile(temporary, uuid);
            fillInMediaFile(mediaFile, permanentPath, temporary, uuid);

            final Instant expiresAt = redisRepository.save(mediaFile);
            return mediaFileMapper.toDto(mediaFile, expiresAt);
        } finally {
            storageService.deleteFile(temporary);
        }
    }

    @Override
    public void cleanUpMediaFiles() {
        final Instant now = Instant.now();

        final Set<String> expiredIds = redisRepository.findExpiredOriginFileId(now);
        if (expiredIds.isEmpty()) {
            return;
        }

        final List<CompletableFuture<String>> futures = expiredIds.stream()
            .map(expiredId ->
                CompletableFuture.supplyAsync(() -> {
                        storageService.deleteDirectoryById(expiredId);
                        return expiredId;
                    }, executor)
                    .exceptionally(ex -> {
                        log.error("Failed to delete directory for originFileId={}", expiredId, ex);
                        return null;
                    })
            )
            .toList();
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        final Set<String> succeedDeletedFilesId = futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (succeedDeletedFilesId.isEmpty()) {
            return;
        }

        try {
            redisRepository.deleteFromHash(succeedDeletedFilesId);
        } catch (final Exception ex) {
            log.error("Failed to delete file in hash with originFileId={}", succeedDeletedFilesId);
            return;
        }
        redisRepository.deleteFromSet(succeedDeletedFilesId);
    }

    private void fillInMediaFile(
        final MediaFile mediaFile,
        final String permanentPath,
        final StoredMedia storedMedia,
        final UUID uuid
    ) {
        mediaFile.setId(uuid);
        mediaFile.setOriginFileId(uuid);
        mediaFile.setOriginName(storedMedia.originalName());
        mediaFile.setPath(permanentPath);
        mediaFile.setCreatedAt(Instant.now());
    }

    @PreDestroy
    void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (final InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
