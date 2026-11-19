package com.media.flow.exception;

import com.media.flow.enums.ErrorCategory;
import lombok.Getter;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 19.11.2026
 */
@Getter
public class BusinessException extends AppException {
    private final ErrorCategory errorCategory;

    public BusinessException(final ErrorCategory errorCategory, final String message) {
        super(message);
        this.errorCategory = errorCategory;
    }
}
