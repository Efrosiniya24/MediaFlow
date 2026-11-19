package com.media.flow.exception;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 30.07.2026
 */
public class AppException extends RuntimeException {

    public AppException(final String message) {
        super(message);
    }

    public AppException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
