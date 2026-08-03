package com.media.flow.service.impl;

import com.media.flow.dto.MediaFileDto;
import com.media.flow.enums.MediaType;
import com.media.flow.exception.FfmpegException;
import com.media.flow.service.FfmpegService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 30.07.2026
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FfmpegServiceImpl implements FfmpegService {
    private final ObjectMapper objectMapper;

    @Override
    public MediaFileDto getVideoParams(final Path filePath) {
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(
                "ffprobe",
                "-v", "quiet",
                "-print_format", "json",
                "-show_format",
                "-show_streams",
                filePath.toAbsolutePath().toString()
            );

            final Process process = processBuilder.start();
            final String json = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            final int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new FfmpegException("Ffprobe failed with exit code " + exitCode);
            }
            return mapToDto(filePath, objectMapper.readTree(json));
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new FfmpegException("Failed to probe media file");
        } catch (final IOException ex) {
            throw new FfmpegException("Failed to probe media file");
        }
    }

    private MediaFileDto mapToDto(final Path filePath, final JsonNode root) {
        final JsonNode format = root.path("format");
        final JsonNode streams = root.path("streams");
        JsonNode video = null;
        JsonNode audio = null;
        for (final JsonNode stream : streams) {
            final String type = stream.path("codec_type").asText();
            if (Objects.equals("video", type) && Objects.isNull(video)) {
                video = stream;
            }
            if (Objects.equals("audio", type) && Objects.isNull(audio)) {
                audio = stream;
            }
        }
        final MediaType mediaType = Objects.nonNull(video) ? MediaType.VIDEO : MediaType.AUDIO;
        return MediaFileDto.builder()
            .fileName(filePath.getFileName().toString())
            .mediaType(mediaType)
            .duration(format.path("duration").asDouble(0))
            .sizeBytes(format.path("size").asLong(0))
            .format(format.path("format_name").asText(null))
            .width(video != null ? video.path("width").asInt(0) : null)
            .height(video != null ? video.path("height").asInt(0) : null)
            .videoCodec(video != null ? video.path("codec_name").asText(null) : null)
            .audioCodec(audio != null ? audio.path("codec_name").asText(null) : null)
            .build();
    }
}
