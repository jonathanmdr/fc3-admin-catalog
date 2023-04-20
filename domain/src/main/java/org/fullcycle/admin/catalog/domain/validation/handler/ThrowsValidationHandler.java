package org.fullcycle.admin.catalog.domain.validation.handler;

import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;

public class ThrowsValidationHandler implements ValidationHandler {

    private ThrowsValidationHandler() { }

    public static ThrowsValidationHandler create() {
        return new ThrowsValidationHandler();
    }

    @Override
    public ThrowsValidationHandler append(final Error error) {
        throw DomainException.with(error);
    }

    @Override
    public ThrowsValidationHandler append(final ValidationHandler handler) {
        throw DomainException.with(handler.getErrors());
    }

    @Override
    public <T> T validate(final Validation<T> validation) {
        try {
            return validation.validate();
        } catch (final Exception ex) {
            throw DomainException.with(new Error(ex.getMessage()));
        }
    }

}
