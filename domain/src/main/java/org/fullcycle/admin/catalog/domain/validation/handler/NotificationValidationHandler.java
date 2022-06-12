package org.fullcycle.admin.catalog.domain.validation.handler;

import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationValidationHandler implements ValidationHandler {

    private final List<Error> errors;

    private NotificationValidationHandler(final List<Error> errors) {
        this.errors = errors;
    }

    public static NotificationValidationHandler create() {
        return new NotificationValidationHandler(new ArrayList<>());
    }

    public static NotificationValidationHandler create(final Error error) {
        return new NotificationValidationHandler(new ArrayList<>()).append(error);
    }

    public static NotificationValidationHandler create(final Throwable throwable) {
        return new NotificationValidationHandler(new ArrayList<>()).append(new Error(throwable.getMessage()));
    }

    @Override
    public NotificationValidationHandler append(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public NotificationValidationHandler append(final ValidationHandler handler) {
        this.errors.addAll(handler.getErrors());
        return this;
    }

    @Override
    public NotificationValidationHandler validate(final Validation validation) {
        try {
            validation.validate();
        } catch (DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (Throwable ex) {
            this.errors.add(new Error(ex.getMessage()));
        }
        return this;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }

}
