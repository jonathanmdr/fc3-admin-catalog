package org.fullcycle.admin.catalog.domain.exception;

import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;

public class NotificationValidationException extends DomainException {

    public NotificationValidationException(final String message, final NotificationHandler notification) {
        super(message, notification.getErrors());
    }

}
