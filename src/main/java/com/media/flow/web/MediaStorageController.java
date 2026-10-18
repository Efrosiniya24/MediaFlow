package com.media.flow.web;

import static org.springframework.http.ContentDisposition.attachment;
import com.media.flow.dto.MediaFileDto;
import com.media.flow.service.DownloadResult;
import com.media.flow.service.DownloadResult.LocalFile;
import com.media.flow.service.DownloadResult.RemoteUrl;
import com.media.flow.service.MediaFileOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 29.07.2026
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
public class MediaStorageController {
    final MediaFileOperationsService mediaFIleOperationsService;

    @PostMapping("/upload")
    public ResponseEntity<MediaFileDto> upload(final MultipartFile file) {
        return ResponseEntity.ok(mediaFIleOperationsService.uploadAndGetMediaFileData(file));
    }

    @GetMapping("/{originalFileId}/{id}/download")
    public ResponseEntity<?> download(final @PathVariable UUID originalFileId, final @PathVariable UUID id) {
        final DownloadResult result = mediaFIleOperationsService.getDataForDownloading(originalFileId, id);

        return switch (result) {
            case LocalFile localFile -> ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    attachment().filename(localFile.fileName(), StandardCharsets.UTF_8).build().toString()
                )
                .contentLength(localFile.size())
                .contentType(MediaType.parseMediaType(localFile.contentType()))
                .body(new FileSystemResource(localFile.path()));

            case RemoteUrl remoteUrl -> ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(remoteUrl.url().toString()))
                .build();
        };
    }
}
