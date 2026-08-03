package com.media.flow.web;

import com.media.flow.dto.MediaFileDto;
import com.media.flow.service.MediaFileOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
}
