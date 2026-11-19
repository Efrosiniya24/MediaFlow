package com.media.flow.web;

import com.media.flow.enums.ErrorCategory;
import com.media.flow.exception.BusinessException;
import com.media.flow.exception.InfrastructureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 30.07.2026
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(final BusinessException exception) {
        final ErrorCategory errorCategory = exception.getErrorCategory();
        final String errorMessage = exception.getMessage();
        log.warn("Business Error [{}]: {}", errorCategory, errorMessage);

        final HttpStatus httpStatus = mapToHttpStatus(errorCategory);
        return ProblemDetail.forStatusAndDetail(httpStatus, errorMessage);
    }

    @ExceptionHandler({ InfrastructureException.class, Exception.class })
    public ProblemDetail handleException(final Exception exception) {
        log.error("Internal server error", exception);
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal server error. Please try again later"
        );
    }

    private HttpStatus mapToHttpStatus(final ErrorCategory errorCategory) {
        return switch (errorCategory) {
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            case FILE_NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
    }
}

