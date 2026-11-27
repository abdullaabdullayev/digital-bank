package com.digitalbank.common.error;

import org.springframework.http.HttpStatus;

/**
 * A stable, machine-readable error identifier.
 * <p>
 * Each service defines its own enum implementing this interface (for example
 * {@code ACCOUNT_NOT_FOUND}, {@code INSUFFICIENT_FUNDS}). Clients branch on {@link #code()},
 * never on the human-readable message, so messages can change without breaking integrations.
 */
public interface ErrorCode {

    /** Upper snake case identifier returned to clients, e.g. {@code INSUFFICIENT_FUNDS}. */
    String code();

    HttpStatus status();

    /** Short human-readable summary used as the problem title. */
    String title();
}
