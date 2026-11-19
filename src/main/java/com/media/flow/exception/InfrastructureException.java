package com.media.flow.exception;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 19.11.2026
 */
public class InfrastructureException extends AppException {
    public InfrastructureException(final String message) {
        super(message);
    }

    public InfrastructureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
