package com.media.flow.web;

import static org.mockito.Mockito.when;
import static org.springframework.http.ContentDisposition.attachment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.media.flow.service.DownloadResult.LocalFile;
import com.media.flow.service.DownloadResult.RemoteUrl;
import com.media.flow.service.MediaFileOperationsService;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 20.20.2026
 */
@WebMvcTest(MediaStorageController.class)
class MediaStorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaFileOperationsService mediaFileOperationsService;

    @TempDir
    private Path tempDirPath;

    @Test
    @DisplayName("GET /media/{originalFileId}/{id}/download success downloading from Local disk")
    void successLocalDiskDownload() throws Exception {
        //given
        final UUID originalFileId = UUID.randomUUID();
        final UUID id = UUID.randomUUID();
        final String fileName = "video.mp4";
        final Path filePath = tempDirPath.resolve(fileName);
        Files.writeString(filePath, fileName, StandardCharsets.UTF_8);
        final long fileSize = Files.size(filePath);

        final LocalFile localFile = new LocalFile(fileName, "video/mp4", filePath.toString(), fileSize);
        when(mediaFileOperationsService.getDataForDownloading(originalFileId, id)).thenReturn(localFile);

        //when
        final ResultActions result = mockMvc.perform(get("/media/{originalFileId}/{id}/download", originalFileId, id));

        //then
        result
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                attachment().filename(localFile.fileName(), StandardCharsets.UTF_8).build().toString())
            )
            .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, fileSize))
            .andExpect(content().contentType("video/mp4"))
            .andExpect(content().string(fileName));
    }

    @Test
    @DisplayName("GET /media/{originalFileId}/{id}/download success downloading from remote url")
    void successRemoteUrlDownload() throws Exception {
        //given
        final UUID originalFileId = UUID.randomUUID();
        final UUID id = UUID.randomUUID();
        final URL remoteUrl = URI.create("https://example.com").toURL();
        final RemoteUrl remoteFile = new RemoteUrl(remoteUrl);

        when(mediaFileOperationsService.getDataForDownloading(originalFileId, id)).thenReturn(remoteFile);

        //when
        final ResultActions result = mockMvc.perform(get("/media/{originalFileId}/{id}/download", originalFileId, id));

        //then
        result
            .andExpect(status().isFound())
            .andExpect(redirectedUrl(remoteUrl.toString()));
    }
}