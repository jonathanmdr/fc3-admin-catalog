package org.fullcycle.admin.catalog.domain.exception;

import org.fullcycle.admin.catalog.domain.validation.Error;

import java.util.List;

public class DomainException extends RuntimeException {

    private final List<Error> errors;

    private DomainException(final List<Error> errors) {
        super("", null, true, false);
        this.errors = errors;
    }

    public static DomainException with(final List<Error> errors) {
        return new DomainException(errors);
    }

    public List<Error> getErrors() {
        return errors;
    }

}
