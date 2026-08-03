package com.media.flow.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 30.07.2026
 */
@Getter
public class AppException extends RuntimeException {
    private final HttpStatus httpStatus;

    public AppException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
