package com.media.flow.service;

import java.net.URL;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 18.10.2026
 */
public sealed interface DownloadResult {
    record LocalFile(String fileName, String contentType, String path, Long size) implements DownloadResult {
    }

    record RemoteUrl(URL url) implements DownloadResult {
    }
}
