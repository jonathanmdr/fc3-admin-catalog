package org.fullcycle.admin.catalog.infrastructure.api.controllers;

import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUncaughtException(final Exception exception) {
        return ResponseEntity.internalServerError()
            .body(ApiError.from(exception));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(final DomainException exception) {
        return ResponseEntity.unprocessableEntity()
            .body(ApiError.from(exception));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(final NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiError.from(exception));
    }

    public record ApiError(String message, List<Error> errors) {
        static ApiError from(final DomainException exception) {
            return new ApiError(exception.getMessage(), exception.getErrors());
        }

        static ApiError from(final Exception exception) {
            return new ApiError(exception.getMessage(), Collections.emptyList());
        }
    }

}
