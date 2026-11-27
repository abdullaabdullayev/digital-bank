package com.digitalbank.common.error;

import java.util.Map;
import java.util.Objects;

/**
 * Expected, domain-level failure (insufficient funds, account closed, ...).
 * <p>
 * It is translated into an RFC 7807 problem response by {@link GlobalExceptionHandler}.
 * Unexpected failures should stay as ordinary runtime exceptions so they surface as 500s.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public BusinessException(ErrorCode errorCode, String message) {
        this(errorCode, message, Map.of());
    }

    public BusinessException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
        this.details = Map.copyOf(details);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /** Extra, non-sensitive fields added to the problem body (e.g. the account number). */
    public Map<String, Object> getDetails() {
        return details;
    }
}
