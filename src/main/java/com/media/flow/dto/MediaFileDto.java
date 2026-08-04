package com.media.flow.dto;

import com.media.flow.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 29.07.2026
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaFileDto {
    private UUID id;
    private UUID originFileId;
    private String fileName;
    private MediaType mediaType;
    private Double duration;
    private Long sizeBytes;
    private String format;
    private Integer width;
    private Integer height;
    private String videoCodec;
    private String audioCodec;
    private Instant expiresAt;
}
