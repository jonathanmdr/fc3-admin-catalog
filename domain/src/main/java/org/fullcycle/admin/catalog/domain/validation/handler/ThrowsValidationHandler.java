package org.fullcycle.admin.catalog.domain.validation.handler;

import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.util.Collections;
import java.util.List;

public class ThrowsValidationHandler implements ValidationHandler {

    @Override
    public ThrowsValidationHandler append(final Error error) {
        throw DomainException.with(error);
    }

    @Override
    public ThrowsValidationHandler append(final ValidationHandler handler) {
        throw DomainException.with(handler.getErrors());
    }

    @Override
    public ThrowsValidationHandler validate(final Validation validation) {
        try {
            validation.validate();
        } catch (final Exception ex) {
            throw DomainException.with(new Error(ex.getMessage()));
        }

        return this;
    }

    @Override
    public List<Error> getErrors() {
        return Collections.emptyList();
    }

}
