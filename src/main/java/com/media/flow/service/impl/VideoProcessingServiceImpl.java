package com.media.flow.service.impl;

import com.media.flow.dto.MediaFileDto;
import com.media.flow.enums.MediaType;
import com.media.flow.exception.BadRequestException;
import com.media.flow.mapper.MediaFileMapper;
import com.media.flow.model.MediaFile;
import com.media.flow.record.StoredMedia;
import com.media.flow.repository.MediaFileRedisRepository;
import com.media.flow.service.FfmpegService;
import com.media.flow.service.StorageService;
import com.media.flow.service.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 17.10.2026
 */
@Service
@RequiredArgsConstructor
public class VideoProcessingServiceImpl implements VideoProcessingService {
    private final MediaFileRedisRepository redisRepository;
    private final FfmpegService ffmpegService;
    private final StorageService storageService;
    private final MediaFileMapper mediaFileMapper;

    @Override
    public MediaFileDto extractAudio(final UUID originalFileId, final UUID id) {
        final MediaFile mediaFile = redisRepository.findByOriginFileIdAndId(originalFileId, id);

        if (mediaFile.getMediaType() != MediaType.VIDEO) {
            throw new BadRequestException("File is not a video");
        }

        if (Objects.isNull(mediaFile.getAudioCodec())) {
            throw new BadRequestException("Video without audio");
        }

        final String newFileName = audioFileName(mediaFile);
        final Path sourcePath = Path.of(mediaFile.getPath());
        final StoredMedia storedMedia = storageService.reserveDerivedFile(sourcePath, newFileName);

        ffmpegService.extractAudio(sourcePath, storedMedia.localPath());
        final MediaFileDto mediaFileDto = ffmpegService.getVideoParams(storedMedia.localPath());
        final MediaFile audio = mediaFileMapper.toEntity(mediaFileDto);
        final String path = storedMedia.localPath().toString();
        audio.setId(storedMedia.id());
        audio.setOriginName(storedMedia.originalName());
        audio.setOriginFileId(mediaFile.getOriginFileId());
        audio.setPath(path);
        audio.setMediaType(MediaType.AUDIO);
        audio.setContentType(contentType(storedMedia.originalName()));
        audio.setCreatedAt(Instant.now());

        final Instant expiresAt = redisRepository.save(audio);
        return mediaFileMapper.toDto(audio, expiresAt, path);
    }

    private String audioFileName(final MediaFile mediaFile) {
        final String originName = mediaFile.getOriginName();
        final int dot = originName.lastIndexOf('.');
        final String baseName = dot > 0 ? originName.substring(0, dot) : originName;
        return baseName + "." + audioExtension(mediaFile.getAudioCodec());
    }

    private String audioExtension(final String audioCodec) {
        if (audioCodec.startsWith("pcm_")) {
            return "wav";
        }
        return switch (audioCodec) {
            case "mp3" -> "mp3";
            case "opus" -> "opus";
            case "vorbis" -> "ogg";
            case "flac" -> "flac";
            default -> "m4a";
        };
    }

    private String contentType(final String fileName) {
        return MediaTypeFactory.getMediaType(fileName)
            .map(org.springframework.http.MediaType::toString)
            .orElse(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }
}
