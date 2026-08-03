package com.media.flow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 30.07.2026
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleException(final AppException exception) {
        final HttpStatus httpStatus = exception.getHttpStatus();
        return ResponseEntity
            .status(httpStatus)
            .body(new ExceptionResponse(exception.getMessage(), httpStatus.value()));
    }

    private record ExceptionResponse(String message, int httpStatus) {
    }
}
