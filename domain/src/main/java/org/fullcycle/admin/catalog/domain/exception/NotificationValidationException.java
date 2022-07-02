package org.fullcycle.admin.catalog.domain.exception;

import org.fullcycle.admin.catalog.domain.validation.handler.NotificationValidationHandler;

public class NotificationValidationException extends DomainException {

    public NotificationValidationException(final String message, final NotificationValidationHandler notification) {
        super(message, notification.getErrors());
    }

}
