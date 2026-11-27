package com.digitalbank.common.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Translates exceptions into RFC 7807 {@code application/problem+json} responses.
 * <p>
 * Every error body has the same shape, so clients handle failures uniformly:
 * <pre>
 * {
 *   "type": "urn:digital-bank:error:insufficient-funds",
 *   "title": "Insufficient funds",
 *   "status": 422,
 *   "detail": "Account AZ.. has 10.00 AZN, 50.00 AZN requested",
 *   "code": "INSUFFICIENT_FUNDS"
 * }
 * </pre>
 * Internal exception messages are never exposed for unexpected errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex) {
        ProblemDetail problem = problem(ex.getErrorCode(), ex.getMessage());
        ex.getDetails().forEach(problem::setProperty);
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("field", e.getField(), "message", String.valueOf(e.getDefaultMessage())))
                .toList();
        ProblemDetail problem = problem(CommonErrorCode.VALIDATION_FAILED, "One or more fields are invalid");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex) {
        return problem(CommonErrorCode.MALFORMED_REQUEST, "Request body is missing or malformed");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return problem(CommonErrorCode.INTERNAL_ERROR, "An unexpected error occurred");
    }

    static ProblemDetail problem(ErrorCode code, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(code.status(), detail);
        problem.setTitle(code.title());
        problem.setType(URI.create("urn:digital-bank:error:" + code.code().toLowerCase().replace('_', '-')));
        problem.setProperty("code", code.code());
        return problem;
    }
}
