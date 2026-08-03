package com.media.flow.exception;

import org.springframework.http.HttpStatus;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 30.07.2026
 */
public class StorageException extends AppException {
    public StorageException(final String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
