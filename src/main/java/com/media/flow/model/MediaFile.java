package com.media.flow.model;

import com.media.flow.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 02.08.2026
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {
    private UUID id;
    private UUID originFileId;
    private String originName;
    private String path;
    private MediaType mediaType;
    private Double duration;
    private Long sizeBytes;
    private String format;
    private Integer width;
    private Integer height;
    private String videoCodec;
    private String audioCodec;
    private Instant createdAt;
}
