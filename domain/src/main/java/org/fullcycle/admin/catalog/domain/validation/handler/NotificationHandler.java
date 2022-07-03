package org.fullcycle.admin.catalog.domain.validation.handler;

import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationHandler implements ValidationHandler {

    private final List<Error> errors;

    private NotificationHandler(final List<Error> errors) {
        this.errors = errors;
    }

    public static NotificationHandler create() {
        return new NotificationHandler(new ArrayList<>());
    }

    public static NotificationHandler create(final Error error) {
        return new NotificationHandler(new ArrayList<>()).append(error);
    }

    public static NotificationHandler create(final Throwable throwable) {
        return new NotificationHandler(new ArrayList<>()).append(new Error(throwable.getMessage()));
    }

    @Override
    public NotificationHandler append(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public NotificationHandler append(final ValidationHandler handler) {
        this.errors.addAll(handler.getErrors());
        return this;
    }

    @Override
    public <T> T validate(final Validation<T> validation) {
        try {
            return validation.validate();
        } catch (DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (Exception ex) {
            this.errors.add(new Error(ex.getMessage()));
        }

        return null;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }

}
