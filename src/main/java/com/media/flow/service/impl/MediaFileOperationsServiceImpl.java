package com.media.flow.service.impl;

import com.media.flow.dto.MediaFileDto;
import com.media.flow.mapper.MediaFileMapper;
import com.media.flow.model.MediaFile;
import com.media.flow.record.StoredMedia;
import com.media.flow.repository.MediaFileRedisRepository;
import com.media.flow.service.FfmpegService;
import com.media.flow.service.MediaFileOperationsService;
import com.media.flow.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 29.07.2026
 */
@Service
@RequiredArgsConstructor
public class MediaFileOperationsServiceImpl implements MediaFileOperationsService {
    private final StorageService storageService;
    private final FfmpegService ffmpegService;
    private final MediaFileRedisRepository redisRepository;
    private final MediaFileMapper mediaFileMapper;

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
}
