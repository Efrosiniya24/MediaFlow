package com.media.flow.web;

import com.media.flow.dto.MediaFileDto;
import com.media.flow.service.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 17.10.2026
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoProcessingController {
    private final VideoProcessingService videoProcessingService;

    @PostMapping("/{originFileId}/{id}/extract-audio")
    public ResponseEntity<MediaFileDto> extractAudio(
        final @PathVariable UUID originFileId,
        final @PathVariable UUID id
    ) {
        final MediaFileDto mediaFileDto = videoProcessingService.extractAudio(originFileId, id);
        final URI location = createLocationForDownload(originFileId, mediaFileDto.getId());
        return ResponseEntity.created(location).body(mediaFileDto);
    }

    private URI createLocationForDownload(final UUID originalFileId, final UUID id) {
        return URI.create("/media/" + originalFileId + "/" + id + "/download");
    }
}
